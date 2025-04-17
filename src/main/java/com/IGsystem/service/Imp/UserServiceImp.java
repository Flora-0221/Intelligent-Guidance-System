package com.IGsystem.service.Imp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.IGsystem.dto.*;
import com.IGsystem.mapper.UserMapper;
import com.IGsystem.service.UserService;
import com.IGsystem.utils.RedisConstants;
import com.IGsystem.utils.RegexUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.IGsystem.entity.User;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import static com.IGsystem.utils.SystemConstants.USER_NICK_NAME_PREFIX;


@Service
@Slf4j
public class UserServiceImp extends ServiceImpl<UserMapper,User> implements UserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户名查询用户
     * @param username 前端传入的用户名
     * @return 返回结果函数
     */
    @Override
    public Result findUserByName(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("nick_name",username);
        User user = getOne(queryWrapper);
        if (user != null){
            return Result.fail("The user already exists");
        }else {
            return Result.ok();
        }

    }

    @Override
    public Result updateUser(UserDTO user) {
        // 检查用户是否存在


        User existingUser = userMapper.selectById(user.getId());
        if (existingUser!= null){
            // 更新用户信息
            existingUser.setNickName(user.getNickName());
            existingUser.setEmail(user.getEmail());
            existingUser.setGender(user.getGender());
            existingUser.setBirthdate(user.getBirthdate());
            existingUser.setAge(user.getAge());
            existingUser.setIcon(user.getIcon());
            existingUser.setPhone(user.getPhone());
            existingUser.setAddress(user.getAddress());
            existingUser.setComment(user.getComment());
            existingUser.setCountry(user.getCountry());
            existingUser.setOccupation(user.getOccupation());
            // 保存更新后的用户信息
            userMapper.updateById(existingUser);
            //保存到redis中
            save2Redis(existingUser);
            return Result.ok();

        }else {
            throw new RuntimeException("User not found"); // 用户不存在，抛出异常
        }

    }



    /**
     * 实现登录功能
     * @param loginForm 登录数据
     * @param session 当前会话
     * @return Result
     */
    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        String phone = loginForm.getPhone();
        //1.校验手机号
        if(RegexUtils.isPhoneInvalid(phone))
        {
            //不符合,返回错误信息
            return Result.fail("The phone number is in the wrong format");
        }

        //根据手机号查询用户
        User user = query().eq("phone", phone).one();

        //判断用户是否存在
        if(user == null){
            //不存在，返回错误提示
//            user = createUserWithPhone(phone);
            return Result.fail("If you don't have the user, please register first");
        }
        //存在验证密码
        String password = user.getPassword();
        if(password.equals(loginForm.getPassword())){
            // 写入redis
            String token = save2Redis(user);

            //返回token
            return Result.ok(token);
        }

       //密码验证失败
        return Result.fail("Wrong password");

    }

    /**
     * 实现注册功能
     * @param registerFormDTO 注册数据
     * @param session 当前会话
     * @return Result
     */
    @Override
    public Result register(RegisterFormDTO registerFormDTO, HttpSession session) {
        String phone = registerFormDTO.getPhone();

        //1.校验手机号
        if(RegexUtils.isPhoneInvalid(phone))
        {
            //不符合,返回错误信息
            return Result.fail("The phone number is in the wrong format");
        }

        //校验密码
        String password = registerFormDTO.getPassword();
        if(RegexUtils.isPasswordInvalid(password))
        {
            //不符合,返回错误信息
            return Result.fail("The password is formatted in the wrong way");
        }
        //判断用户是否存在
        //根据手机号查询用户
        User user = query().eq("phone", phone).one();
        if(user != null){
            //存在，返回错误提示
            return Result.fail("The user already exists, please log in directly");
        }
        //不存在，创建该用户
        user = createUser(registerFormDTO);
        //将用户存入Redis
        String token = save2Redis(user);
        return Result.ok(token);
    }


    private User createUser(RegisterFormDTO registerFormDTO) {
        User user = new User();
        user.setPhone(registerFormDTO.getPhone());
        user.setPassword(registerFormDTO.getPassword());
        user.setEmail(registerFormDTO.getEmail());
        user.setGender(Gender.valueOf(registerFormDTO.getGender()));
        user.setOccupation(Occupation.valueOf(registerFormDTO.getOccupation()));
        user.setBirthdate(registerFormDTO.getBirthdate());
        user.setAge(registerFormDTO.getAge());
        if(registerFormDTO.getNickName() == null || registerFormDTO.getNickName().isBlank()){
            //如果没有传入昵称
            user.setNickName(USER_NICK_NAME_PREFIX+ RandomUtil.randomString(10));
        }
        else {
            user.setNickName(registerFormDTO.getNickName());
        }
        save(user);
        return user;
    }
    private String save2Redis(User user){
        //随机生成token作为登录令牌
        //TODO 用JWT生成token
        String token = UUID.randomUUID().toString(true);

        //将User对象转换为HashMap存储
        String tokenKey =  RedisConstants.LOGIN_USER_KEY+ token;
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO,new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName,fieldValue)->{
                            if (fieldValue == null) {
                                // 为null的字段提供默认值或者特殊处理
                                return ""; // 举例：返回默认值
                            }
                            return fieldValue.toString();
                        }
                        ));

        //存储
        stringRedisTemplate.opsForHash().putAll(tokenKey,userMap);
        //设置有效期
        stringRedisTemplate.expire(tokenKey,RedisConstants.LOGIN_USER_TTL,TimeUnit.MINUTES);
        return token;

    }
}
