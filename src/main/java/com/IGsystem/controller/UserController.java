package com.IGsystem.controller;

import com.IGsystem.dto.*;
import com.IGsystem.mapper.FolderMapper;
import com.IGsystem.mapper.CompanyMatchMapper;

import com.IGsystem.service.FavoriteService;
import com.IGsystem.service.UserService;
import com.IGsystem.utils.UserHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Flora_YangFY
 * @since 2024-3-4
 */

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*") // 允许所有域名的请求
@Slf4j
public class UserController {

    @Value("${IG.path}")
    private String basepath;

    @Resource
    private UserService userService;

    @Autowired
    private FolderMapper folderMapper;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private CompanyMatchMapper companyMatchMapper;



    /**
     * 实现登录功能
     * @param loginFormDTO 登录的表单数据
     * @param session 当前会话
     * @return Result
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginFormDTO, HttpSession session){
        //登录功能
        return userService.login(loginFormDTO,session);
//        return Result.fail("功能未完成");
    }

    /**
     * 用户注册功能
     * @param registerFormDTO 用户注册表信息
     * @param session 当前会话
     * @return 返回结果类
     */
    @PostMapping("/register")
    public Result register(@RequestBody RegisterFormDTO registerFormDTO, HttpSession session){
        return userService.register(registerFormDTO,session);
    }

    /**
     * 用户获取身份信息
     * @return 返回用户信息
     */
    @GetMapping("/me")
    public Result me(){
        // 获取当前登录的用户并返回

        UserDTO user = UserHolder.getUser();
        if(user == null){
            return Result.fail("用户不存在");
        }
        return Result.ok(user);
//        return Result.fail("功能未完成");
    }


    /**
     * 查询用户名是否存在
     * @param userName 用户名
     * @return 结果类
     */
    @GetMapping("/validateName/validateName/")
    public Result validateName(@RequestParam("username") String userName){
        return userService.findUserByName(userName);

    }


    /**
     * 头像上传
     * @param request 请求
     * @param file 头像文件
     * @return 返回类
     */
    @PostMapping("/upload")
    public Result upload(HttpServletRequest request, MultipartFile file){
        //file是一个临时文件，需要转存到注定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID重新生成文件名防止文件名称重复造成文件覆盖
        String filename = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象
        File dir = new File(basepath);

        //判断当前目录是否存在
        if(!dir.exists()){
            //目录不存在，需要创建
            dir.mkdirs();
        }
        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basepath + filename));
            return Result.ok(filename);

        }catch (IOException e) {
            e.printStackTrace();
        }
        return Result.ok(filename);
    }

    /**
     * 更新用户信息

     * @param user 用户信息
     * @return 返回类
     */
    @PutMapping("/update")
    public Result updateUser( @RequestBody UserDTO user) {
                Long id = UserHolder.getUser().getId();
                user.setId(id);
        System.out.println(user);
                userService.updateUser(user);
                return Result.ok("update success");
    }

    /**
     * 获取头像
     * @param name 用户名
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basepath + name));
            //输出流，通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            //读取文件中的内容并将其写入输出流
            while((len = fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                //将缓冲区中的数据立即刷新/写入到目标设备，以确保数据的完整性
                outputStream.flush();
            }
            //关闭资源
            fileInputStream.close();
            outputStream.close();
            outputStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @GetMapping("/addFolder")
    public Result addFolder(String folderName, String description){
        if (folderName.length() > 15) {
            return Result.fail("收藏夹名称长度不能超过15个字符");
        }

        if (description.length() > 100) {
            return Result.fail("收藏夹描述长度不能超过100个字符");
        }

        Folder folder = new Folder();
        Long userID = UserHolder.getUser().getId();
        folder.setUserId(userID);
        folder.setName(folderName);
        folder.setDescription(description);
        folderMapper.insert(folder);

        return Result.ok();
    }

    @DeleteMapping("/removeFolder")
    public Result removeFolder(@RequestBody Map<String, String> requestBody) {
        return favoriteService.removeFolder(requestBody);
    }


    @GetMapping("/getfolder")
    public Result getUserFolders() {
        Map<String, Object> response = new HashMap<>();
        try {
            // 调用favoriteService获取用户的收藏夹信息
            List<Folder> folders = (List<Folder>) favoriteService.getUserFavoriteFolders().getData();
//            List<Question> questions = (List<Question>) favoriteService.getUserFavoriteQuestions().getData();
            if (folders.isEmpty()) {
                response.put("message", "No favorite folders found");
            } else {
                response.put("data", folders);
//                response.put("questions",questions);
                response.put("defaultTabIndex", 0);  // 设置默认选中的tab索引
            }
            return Result.ok(response);
        } catch (Exception e) {
            return Result.fail("Failed to fetch favorite folders");
        }
    }
    @GetMapping("/saveFavorite")
    public ResponseEntity<String> saveFavorite(@RequestParam int favorite_id, int question_id, @RequestParam String question_type) {
        try {
            Long userId = UserHolder.getUser().getId();

            // 调用 favoriteService 或直接操作数据库，将收藏信息存储到 favorite_questions 表中
            // 示例代码：
            favoriteService.saveFavoriteQuestion(favorite_id,question_id, question_type, userId);

            return ResponseEntity.ok("题目已成功收藏");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("保存收藏题目时出错：" + e.getMessage());
        }
    }


    /**
     * 实现登出
     * @return Result
     */

    @GetMapping("/logout")
    public Result logout(){
        UserHolder.removeUser();
        return Result.ok();
    }

    @PostMapping("/generate-graph")
    public Result generateGraph(@RequestBody String skillName){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("skillName", skillName);

        // 发送POST请求并获取响应
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        String flaskAppUrl = "http://localhost:8888/generate-graph-skill";
        Map<String, String> responseBody = restTemplate.postForObject(flaskAppUrl, requestEntity, Map.class);


        // 获取图表 HTML
        String graphHtml = null;
        if (responseBody != null) {
            graphHtml = responseBody.get("graphHtml");
        }

        // 直接返回图表 HTML
        return Result.ok(graphHtml);
    }

    @PostMapping("/generate-keyword-graph")
    public Result generateKeywordGraph(@RequestBody String keyword){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("keyword", keyword);

        // 发送POST请求并获取响应
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        String flaskAppUrl = "http://localhost:8888/generate-graph-kw";
        Map<String, String> responseBody = restTemplate.postForObject(flaskAppUrl, requestEntity, Map.class);


        // 获取图表 HTML
        String graphHtml = null;
        if (responseBody != null) {
            graphHtml = responseBody.get("graphHtml");
        }


        // 直接返回图表 HTML
        return Result.ok(graphHtml);
    }

    @GetMapping("/getMatch")
    public Result getMatch(String job,String course){
        try {
            // 构建查询条件
            QueryWrapper<companyMatch> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("job_title", job).eq("course_name", course);

            // 执行查询操作
            companyMatch match = companyMatchMapper.selectOne(queryWrapper);

            // 返回查询结果
            if (match != null) {
                return Result.ok(match);
            } else {
                return Result.fail("No match found for the given job and course."); // 没有匹配结果时返回错误信息
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("An error occurred while fetching match."); // 返回错误信息
        }
    }



}
