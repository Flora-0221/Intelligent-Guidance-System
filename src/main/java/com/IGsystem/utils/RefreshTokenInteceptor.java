package com.IGsystem.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.IGsystem.dto.UserDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RefreshTokenInteceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;
    public RefreshTokenInteceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 获取请求头中的token
        String token = request.getHeader("authorization");
        //判断是否为空
        if(StrUtil.isBlank(token))
        {
           return true;
        }
        // 获基于token获取redis中的用户
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(RedisConstants.LOGIN_USER_KEY + token);
        //判断结果是否存在
        if(userMap.isEmpty())
        {
            return true;
        }
        // 将查询到的用户信息转为DTO对象 (不忽略转换过程中遇到的异常)
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);

        //存在，保存用户信息到线程
        UserHolder.saveUser(userDTO);

        // 刷新token的有效期
        stringRedisTemplate.expire(RedisConstants.LOGIN_USER_KEY+token ,RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);

        //放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }


}
