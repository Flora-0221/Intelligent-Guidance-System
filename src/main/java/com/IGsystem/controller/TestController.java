package com.IGsystem.controller;

import com.IGsystem.dto.GradeRequest;
import com.IGsystem.dto.Result;
import com.IGsystem.dto.SAGradeRequest;
import com.IGsystem.dto.TextQuestion;
import com.IGsystem.entity.TestResult;
import com.IGsystem.service.TestService;
import com.IGsystem.service.UserService;
import com.IGsystem.utils.UserHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.injector.methods.DeleteById;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*") // 允许所有域名的请求
@Slf4j
public class TestController {
    @Resource
    private TestService testService;

    @PostMapping("/getGrade")
    public Result getGrade(@RequestBody GradeRequest gradeRequest){
        return testService.getGrade(gradeRequest);
    }

    @GetMapping("/getTest")
    public Result getTest(Long id){
        return testService.getTest(id);
    }

    @PostMapping("/getSAGrade")
    public Result getSAGrade(@RequestBody SAGradeRequest saGradeRequest){
        return testService.getSAGrade(saGradeRequest);
    }

    @GetMapping("/getExplain")
    public Result getExplain(int id) {
        return testService.getExplain(id);
    }

    @GetMapping("/optimizeExplain")
    public Result optimizeExplain(int id, String userFeedback) {
        return testService.optimizeExplain(id,userFeedback);
    }

    /**
     * 分页构造器
     * @param params 获取请求分页的数据
     * @return 返回类
     */
    @PostMapping("/page")
    public Result page(@RequestBody Map<String, Integer> params){

        Long id = UserHolder.getUser().getId();
        Integer page = params.get("page");
        Integer pageSize = params.get("pageSize");
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        log.info("page = {},pageSize = {}" ,page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<TestResult> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(TestResult::getUserId,id);
        //添加排序条件
        queryWrapper.orderByDesc(TestResult::getFinishTime);
        //执行查询
        testService.page(pageInfo,queryWrapper);

        return Result.ok(pageInfo);
    }

    /**
     * 删除操作
     * @param testResult 测试结果对象
     * @return 结果类
     */
    @PostMapping("/delete")
    public Result deleteTest(@RequestBody TestResult testResult) {
        return testService.deleteTest(testResult);
    }

//    @GetMapping("/getLongGrade")
//    public Result getLongGrade(@RequestParam String userAnswer,
//                               @RequestParam String startTime,
//                               @RequestParam String finishTime) {
////        Duration duration = Duration.between(startTime, finishTime);
//        // 构造返回的数据结构
//        Map<String, Object> responseBody = new HashMap<>();
//        Random random = new Random();
//        // 使用 nextInt 方法生成指定范围内的随机整数
////        int randomNumber = random.nextInt(21) + 60;
////        responseBody.put("grade", randomNumber);
//          responseBody.put("grade", 80);
////        responseBody.put("consumingTime", duration.getSeconds());
//        // 返回响应
//        return Result.ok(responseBody);
//    }
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String DEEPSEEK_API_KEY = "sk-9728d679123d4d4d9493f049f39f1849";

    @GetMapping("/getLongGrade")
    public Result getLongGrade(@RequestParam String userAnswer,
                               @RequestParam String startTime,
                               @RequestParam String finishTime) {

        // 构造 system_prompt 和 question
        String systemPrompt = "请详细解释这个问题，并给出具体的例子";
        String question = "Can the same function of a computer system be performed by both hardware and software?";

        // 构造 prompt
        String prompt = String.format(
                "系统提示: %s\n" +
                        "问题: %s\n" +
                        "用户回答: %s\n\n" +
                        "请根据以下标准对回答进行评分（0-100分）：\n" +
                        "1. 回答的准确性\n" +
                        "2. 回答的完整性\n" +
                        "3. 回答的相关性\n" +
                        "4. 语言表达的清晰度\n\n" +
                        "请只返回一个0-100之间的数字作为评分。",
                systemPrompt, question, userAnswer
        );
        System.out.println("构造了提示词");

        // 构造请求体
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "deepseek-chat");
        payload.put("temperature", 0.7);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "你是一个专业的评分系统，请根据给定的标准对回答进行评分。"));
        messages.add(Map.of("role", "user", "content", prompt));
        payload.put("messages", messages);

        // 构造 HTTP 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + DEEPSEEK_API_KEY);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        int score = 50; // 默认分数
        System.out.println("开始尝试发送请求");

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(DEEPSEEK_API_URL, request, String.class);
            System.out.println("获取返回"+response);
            if (response.getStatusCode() == HttpStatus.OK) {
                // 解析返回值中的评分
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> result = mapper.readValue(response.getBody(), Map.class);
                List<Map<String, Object>> choices = (List<Map<String, Object>>) result.get("choices");

                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = ((String) message.get("content")).trim();
                    score = Math.max(0, Math.min(100, Integer.parseInt(content)));
                    System.out.println("获取的评分是"+score);
                }
            } else {
                System.out.println("DeepSeek 请求失败：" + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("grade", score);
        return Result.ok(responseBody);
    }



}
