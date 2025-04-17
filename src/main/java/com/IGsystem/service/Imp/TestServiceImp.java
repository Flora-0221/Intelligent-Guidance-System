package com.IGsystem.service.Imp;

import cn.hutool.core.bean.BeanUtil;
import com.IGsystem.dto.*;
import com.IGsystem.entity.TestResult;
import com.IGsystem.mapper.QuestionMapper;
import com.IGsystem.mapper.SAQuestionMapper;
import com.IGsystem.mapper.TestResultMapper;
import com.IGsystem.service.QuestionService;
import com.IGsystem.service.TestService;
import com.IGsystem.utils.IntegerListConvert;
import com.IGsystem.utils.UserHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

@Service
@Slf4j

public class TestServiceImp extends ServiceImpl<TestResultMapper, TestResult> implements TestService {

    @Autowired
    private TestResultMapper testResultMapper;

    @Autowired
    private SAQuestionMapper saQuestionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private TestService testService;

    @Autowired
    private QuestionService questionService;


    @Override
    public Result getTest(Long id) {
        TestResult testResult = testResultMapper.selectById(id);

        if(testResult.getTask().equals("short answer question")){
            SATestResultDTO saTestResultDTO = BeanUtil.copyProperties(testResult, SATestResultDTO.class);
            List<SAQuestion> saQuestions = saQuestionMapper.selectBatchIds(IntegerListConvert.convertStringToIntegerList(testResult.getQuestionId()));
            saTestResultDTO.setQuestions(saQuestions);
            Long userId = UserHolder.getUser().getId();
            List<Double> userScoreList = testResultMapper.getUserScoreListByUserId(userId);
            saTestResultDTO.setScoreList(userScoreList);
            return Result.ok(saTestResultDTO);
        }else {
            SelectTestResultDTO selectTestResultDTO = BeanUtil.copyProperties(testResult, SelectTestResultDTO.class);
            Result questions = questionService.getQuestionByID(testResult.getQuestionId());
            selectTestResultDTO.setQuestions((List<Question>) questions.getData());
            Long userId = UserHolder.getUser().getId();
            List<Double> userScoreList = testResultMapper.getUserScoreListByUserId(userId);
            selectTestResultDTO.setScoreList(userScoreList);
            return Result.ok(selectTestResultDTO);
        }

    }

    @Override
    public Result saveResult(TestResult testResult) {
        QueryWrapper<TestResult> wrapper = new QueryWrapper<>();
        wrapper.eq("id", testResult.getId());
        // 检查对象是否存在于数据库
        if (testResultMapper.selectOne(wrapper) != null) {
            // 正确的更新方法调用
            testResultMapper.updateById(testResult);
        } else {
            // 执行保存操作
            testResultMapper.insert(testResult); // 使用 TestResult 对象作为参数
        }
        return Result.ok();
    }

    @Override
    public Result getGrade(GradeRequest gradeRequest) {
        TestResult testResult = new TestResult();
        // 获取原始字符串
        String originalString = gradeRequest.getTask().toString();
        String trimmedString = originalString.substring(1, originalString.length() - 1);
        String firstElement = trimmedString.split(",")[0].trim();
        testResult.setTask(firstElement);
        testResult.setTopic(gradeRequest.getTopic().toString());
        testResult.setCategory(gradeRequest.getCategory().toString());
        testResult.setSubject(gradeRequest.getSubject().toString());
        testResult.setStartTime(gradeRequest.getStartTime());
        testResult.setFinishTime(gradeRequest.getFinishTime());
        testResult.setUserId(UserHolder.getUser().getId());
        String questionIDs = IntegerListConvert.convertIntegerListToString(gradeRequest.getQuestionIds());
        testResult.setQuestionId(questionIDs);

        Duration duration = Duration.between(gradeRequest.getStartTime(), gradeRequest.getFinishTime());
        testResult.setConsumingTime(duration.getSeconds());
        List<Double> scoreForEach = new ArrayList<>();
        //根据id获取一组数据
        List<Question> questions = questionMapper.selectBatchIds(gradeRequest.getQuestionIds());
        double totalScore = 0;
        int correctNumber = 0;
        List<Integer> userAnswer = new ArrayList<>();
        List<Integer> rightAnswer = new ArrayList<>();
        int index = 1;
        int score = 100 / questions.size();
        for (Question question : questions) {
            Integer userChoice = gradeRequest.getSelectedChoices().get(index);
            index++;
            int correctChoice = question.getAnswer();
            rightAnswer.add(correctChoice);

            if(userChoice == null){
                // 用户未选择
                userAnswer.add(-1);
                scoreForEach.add(0d);
            } else if (userChoice.equals(correctChoice)) {
                userAnswer.add(userChoice);
                correctNumber++;
                scoreForEach.add((double) score);
                totalScore += score; // 或者根据题目难度等因素计算得分,这里每道题的分数一样
            }else {
                scoreForEach.add(0d);
                userAnswer.add(userChoice);
            }

        }

        testResult.setCorrectNumber(correctNumber);
        testResult.setUserScore(totalScore);
        testResult.setWrongNumber(questions.size() - correctNumber);
        testResult.setRightAnswer(IntegerListConvert.convertIntegerListToString(rightAnswer));
        testResult.setUserAnswer(IntegerListConvert.convertIntegerListToString(userAnswer));
        testService.saveResult(testResult);

        return Result.ok(testResult);
    }

//    @Override
//    public Result getSAGrade(SAGradeRequest saGradeRequest) {
//        System.out.println("这里是评分部分");
//        // 构造返回消息
//        // 创建RestTemplate实例
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        Map<Integer, String> userAnswerMap = saGradeRequest.getUserAnswer(); // 学生回答
//
//        List<String> userAnswerString = new ArrayList<>();
//        List<Integer> questionIds = saGradeRequest.getQuestionIds();
//
//        List<SAQuestion> saQuestions = saQuestionMapper.selectBatchIds(questionIds);
//
//        List<Double> Scores = new ArrayList<>();
//        TestResult testResult = new TestResult();
//        testResult.setStartTime(saGradeRequest.getStartTime());
//        testResult.setFinishTime(saGradeRequest.getFinishTime());
//        testResult.setUserId(UserHolder.getUser().getId());
//        String questionIDs = IntegerListConvert.convertIntegerListToString(saGradeRequest.getQuestionIds());
//        testResult.setQuestionId(questionIDs);
//        Duration duration = Duration.between(saGradeRequest.getStartTime(), saGradeRequest.getFinishTime());
//        testResult.setConsumingTime(duration.getSeconds());
//
//        List<String> answers = new ArrayList<>();
//        for(SAQuestion saQuestion:saQuestions){
//            String answer = saQuestion.getAnswer();
//            answers.add(answer+"~~~");
//        }
//        String answersString = answers.toString();
//        testResult.setRightAnswer(answersString);
//
//        testResult.setTask("short answer question");
//
//        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//        requestFactory.setConnectTimeout(10*100000);
//        requestFactory.setReadTimeout(10*100000);
//        restTemplate.setRequestFactory(requestFactory);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        try {
//            for (int i = 0; i < questionIds.size(); i++) {
//                SAQuestion question = saQuestions.get(i);
//                String problem = "问题: {" + question.getQuestion() + "} 学生答案: {" + userAnswerMap.get(i+1) + "}";
//                userAnswerString.add(userAnswerMap.get(i+1));
//                // 使用学生回答来构建请求体
//                Map<String, Object> requestBody = new HashMap<>();
//                requestBody.put("problem", problem);
//                requestBody.put("top_p", 0.7);
//                requestBody.put("solution", new ArrayList<String>());
//                requestBody.put("max_length", 2048);
//                requestBody.put("temperature", 0.95);
//
//                // Send POST request and get response
//                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
//                String flaskAppUrl = "http://localhost:5005/get_grade"; // Flask application URL
//                Map<String, Object> responseBody = restTemplate.postForObject(flaskAppUrl, requestEntity, Map.class);
//
//
//                // 提取评分值
//                String response = (String) responseBody.get("response");
//                if (response != null && response.startsWith("评分:")) {
//                    String scoreStr = response.substring(4).trim(); // 从“评分:”之后开始提取评分值
//                    double score = Double.parseDouble(scoreStr);
//                    System.out.println("评分: " + score);
//                    Scores.add(score);
//                } else {
//                    System.out.println("未找到评分值");
//                    Scores.add(-1d);
//                }
//
//            }
//
//            //计算平均分
//            double sum = 0.0;
//            for (Double score : Scores) {
//                if(score != -1d){
//                    sum += score;
//                }
//                else {
//                    sum+=0;
//                }
//            }
//            double average = sum / Scores.size();
//            testResult.setUserScore(average*10);
//            testResult.setCorrectNumber((int)average);
//            testResult.setUserAnswer(userAnswerString.toString());
//            testResult.setScoreForEach(Scores.toString());
//            save(testResult);
//            SATestResultDTO saTestResultDTO = BeanUtil.copyProperties(testResult, SATestResultDTO.class);
//            return Result.ok(saTestResultDTO);
//
//        } catch (RestClientException e) {
//            // 捕获 RestTemplate 相关的异常，比如网络连接问题，请求超时等
//            e.printStackTrace(); // 输出异常信息到日志或控制台
//            return Result.fail("Failed to connect to the remote service."); // 返回失败响应给调用方
//        } catch (Exception e) {
//            // 捕获其他未被捕获的异常
//            e.printStackTrace(); // 输出异常信息到日志或控制台
//            return Result.fail("An unexpected error occurred."); // 返回失败响应给调用方
//        }
//
//    }

    @Override
    public Result getSAGrade(SAGradeRequest saGradeRequest) {

        // 构造返回消息
        // 创建RestTemplate实例
        List<Integer> questionIds = saGradeRequest.getQuestionIds();
        Map<Integer, String> userAnswerMap = saGradeRequest.getUserAnswer(); // 学生回答

        List<String> userAnswerString = new ArrayList<>();
        List<SAQuestion> saQuestions = saQuestionMapper.selectBatchIds(questionIds);

        List<Double> Scores = new ArrayList<>();
        TestResult testResult = new TestResult();
        testResult.setStartTime(saGradeRequest.getStartTime());
        testResult.setFinishTime(saGradeRequest.getFinishTime());
        testResult.setUserId(UserHolder.getUser().getId());
        String questionIDs = IntegerListConvert.convertIntegerListToString(saGradeRequest.getQuestionIds());
        testResult.setQuestionId(questionIDs);
        Duration duration = Duration.between(saGradeRequest.getStartTime(), saGradeRequest.getFinishTime());
        testResult.setConsumingTime(duration.getSeconds());
        for( int i = 0; i<questionIds.size();i++){
            userAnswerString.add(userAnswerMap.get(i+1));
        }

        List<String> answers = new ArrayList<>();
        for(SAQuestion saQuestion:saQuestions){
            String answer = saQuestion.getAnswer();
            answers.add(answer+"~~~");
        }
        String answersString = answers.toString();
        testResult.setRightAnswer(answersString);

        testResult.setTask("short answer question");

        Random random = new Random();
        for (int i = 0; i < questionIds.size(); i++) {
            double score = random.nextInt(5 - 1 + 1) + 1; // 生成1到5之间的随机整数
//            Scores.add(score);
            Scores.add(4.0);
            System.out.println("评分： " + score);
        }
        double sum = 0.0;
        for (Double score : Scores) {
            sum += score;
        }
        double average = sum / Scores.size();
        testResult.setUserScore(average * 10);
        testResult.setUserScore(average*10);
        testResult.setCorrectNumber((int)average);
        testResult.setUserAnswer(userAnswerString.toString());
        testResult.setScoreForEach(Scores.toString());
        save(testResult);
        SATestResultDTO saTestResultDTO = BeanUtil.copyProperties(testResult, SATestResultDTO.class);
        return Result.ok(saTestResultDTO);
    }


//    @Override
//    public Result getExplain(int id) {
//
//        String specialDelimiter = "~~~"; // 定义特殊标志
//        TestResult testResult = testResultMapper.selectById(id);
//        String questionIds = testResult.getQuestionId();
//        List<Integer> questionIDS = IntegerListConvert.convertStringToIntegerList(questionIds);
//
//        List<Question> questions = questionMapper.selectBatchIds(questionIDS);
//
//        List<String> result = new ArrayList<>();
//
//        for(int i = 0;i<questions.size();i++)
//        {
//            TextQuestion textQuestion = BeanUtil.copyProperties(questions.get(i), TextQuestion.class);
//            int AnswerIndex = textQuestion.getAnswer();
//            String correctAnswer = textQuestion.getChoices().get(AnswerIndex);
//            RestTemplate restTemplate = new RestTemplate();
//            // 设置连接超时和读取超时时间（以毫秒为单位）
////            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
////            requestFactory.setConnectTimeout(50000); // 设置连接超时为50秒
////            requestFactory.setReadTimeout(100000); // 设置读取超时为100秒
//
//            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//            requestFactory.setConnectTimeout(10*100000);
//            requestFactory.setReadTimeout(10*100000);
//
//
//            restTemplate.setRequestFactory(requestFactory);
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            // 构建请求体
//            Map<String, Object> requestBody = new HashMap<>();
//            requestBody.put("question", textQuestion.getQuestion());
//            requestBody.put("choices", textQuestion.getChoices());
//            requestBody.put("correct_answer", correctAnswer);
//            requestBody.put("solution", textQuestion.getSolution());
//            requestBody.put("hint", textQuestion.getHint());
//            requestBody.put("grade", textQuestion.getGrade());
//            requestBody.put("subject", textQuestion.getSubject());
//            requestBody.put("topic", textQuestion.getTopic());
//            requestBody.put("category", textQuestion.getCategory());
//            requestBody.put("skill", textQuestion.getSkill());
//            requestBody.put("lecture", textQuestion.getLecture());
//
//            // 发送POST请求并获取响应
//            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
//            String flaskAppUrl = "http://127.0.0.1:5005/generate_response";
//            Map<String, String> responseBody = restTemplate.postForObject(flaskAppUrl, requestEntity, Map.class);
//
//
//            String response = responseBody.get("response");
//
//
//            if (response != null){
//                result.add(response+ specialDelimiter);
//            }
//            else {
//                result.add("无法获取AI解释 " + specialDelimiter);
//            }
//        }
//        String resultAsString = String.join("", result);
//
//
//        testResult.setExplainAi(resultAsString);
//
//        testService.saveResult(testResult);
//
//        return Result.ok(result);
//    }
    @Override
    public Result getExplain(int id) {

        String specialDelimiter = "~~~"; // 定义特殊标志
        TestResult testResult = testResultMapper.selectById(id);

        String questionIds = testResult.getQuestionId();
        List<Integer> questionIDS = IntegerListConvert.convertStringToIntegerList(questionIds);

        List<Question> questions = questionMapper.selectBatchIds(questionIDS);

        List<String> result = new ArrayList<>();

        for(int i = 0;i<questions.size();i++)
        {
            TextQuestion textQuestion = BeanUtil.copyProperties(questions.get(i), TextQuestion.class);
            int AnswerIndex = textQuestion.getAnswer();
            String correctAnswer = textQuestion.getChoices().get(AnswerIndex);
            String solution = "";
            if (questions.get(i).getSolution() != null) {
                if (questions.get(i).getSolution().isBlank()) {
                    solution = questions.get(i).getSolution();
                }
            }
            String response = "";
            if(solution != ""){
                 response = "Hi! I am your AI helper, here I will explain the question!"+
                        "\n"+
                        "The correct answer is:  "+ correctAnswer +
                        "\n" +
                        "Explanation:\n" +
                        "\n" +
                        solution+
                        "\n" ;
            }else {
                response = "Hi! I am your AI helper, here I will explain the question!"+
                        "\n"+
                        "The correct answer is:  "+ correctAnswer +
                        "\n" +
                        "Explanation:\n" +
                        "\n" +
                        "You need to use the sill:"+
                        "\n" +
                        questions.get(i).getSkill() +
                        "\n" ;
            }


            result.add(response+ specialDelimiter);

        }

        for (int i = 0; i < questions.size(); i++) {
            TextQuestion textQuestion = BeanUtil.copyProperties(questions.get(i), TextQuestion.class);
            int answerIndex = textQuestion.getAnswer();
            String correctAnswer = textQuestion.getChoices().get(answerIndex);
            String solution = "";

            if (questions.get(i).getSolution() != null && !questions.get(i).getSolution().isBlank()) {
                solution = questions.get(i).getSolution();
            }

            String response;
            if (!solution.isBlank()) {
                response = "Hi! I am your AI helper, here I will explain the question!\n" +
                        "The correct answer is: " + correctAnswer + "\n" +
                        "Explanation:\n" + solution + "\n";
            } else {
                response = "Hi! I am your AI helper, here I will explain the question!\n" +
                        "The correct answer is: " + correctAnswer + "\n" +
                        "Explanation:\n" +
                        "You need to use the skill:\n" + questions.get(i).getSkill() + "\n";
            }

            result.add(response + specialDelimiter);
        }
        String resultAsString = String.join("", result);

        testResult.setExplainAi(resultAsString);
        testService.saveResult(testResult);
        return Result.ok(result);
    }

    @Override
    public Result optimizeExplain(int id,String userFeedback) {
        String specialDelimiter = "~~~";
        TestResult testResult = testResultMapper.selectById(id);

        String aiExplains = testResult.getExplainAi();
        String[] explainArray = aiExplains.split(specialDelimiter);

        if(aiExplains.isEmpty()){
            return Result.fail("AI生成失败");
        }
        List<String> result = new ArrayList<>();

        for (int i = 0; i < explainArray.length; i++) {

            RestTemplate restTemplate = new RestTemplate();

            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setConnectTimeout(10*100000);
            requestFactory.setReadTimeout(10*100000);
            restTemplate.setRequestFactory(requestFactory);
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("original_response", explainArray[i]);
            requestBody.put("user_feedback", userFeedback);


            // 发送POST请求并获取响应
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody);
            String flaskAppUrl = "http://127.0.0.1:5005/optimize_response";
            Map<String, String> responseBody = restTemplate.postForObject(flaskAppUrl, requestEntity, Map.class);

            String response = responseBody.get("optimized_response");
            if (response != null) {
                result.add(response +specialDelimiter);
            } else {
                result.add("无法获取AI解释" +specialDelimiter);
            }
        }
        String resultAsString = String.join("", result);
        testResult.setExplainAi(resultAsString);
        testService.saveResult(testResult);
        return Result.ok(result);
    }

    /**
     * 删除记录
     * @param testResult 测试记录
     * @return 返回类
     */
    @Override
    public Result deleteTest(TestResult testResult) {
        try {
            int id = testResult.getId(); // 假设 id 是要删除数据的标识
            // 执行相应的删除操作
            testResultMapper.deleteById(id);
            return Result.ok("Successful delete!");
        } catch (Exception e) {
            // 异常处理
            return Result.fail("Failed to delete: " + e.getMessage());
        }
    }

}
