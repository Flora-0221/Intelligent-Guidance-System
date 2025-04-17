package com.IGsystem.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import com.IGsystem.dto.GradeRequest;
import com.IGsystem.dto.Question;
import com.IGsystem.dto.RegisterFormDTO;
import com.IGsystem.dto.Result;
import com.IGsystem.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Flora_YangFY
 * @since 2024-3-4
 */

@RestController
@RequestMapping("/question")
@CrossOrigin(origins = "*") // 允许所有域名的请求
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @GetMapping("/get")
    public Result getQuestion(){
        return questionService.get();
    }

    @GetMapping("/getImage")
    public Result  getImage(String split, Long id){
       return questionService.getImage(split,id);
    }

    @GetMapping("/getQuestionByID")
    public Result getQuestionByID(String questionids){
        return questionService.getQuestionByID(questionids);
    }

    @GetMapping("/getQuestion")
    public Result get(String task, String grade, String subject, String topic, String category,int questionCount){
        return questionService.getQuestion( task,  grade,  subject,  topic,  category, questionCount);
    }
    @GetMapping("/getQuestionByLabel")
    public Result getFilteredQuestion(@RequestParam String grade, @RequestParam String subject, @RequestParam String task,@RequestParam String category,@RequestParam String topic) {
        return questionService.getByLabel(grade, subject, task, category, topic);
    }

    @GetMapping("/getOption")
    public Result getFilteredOptions(@RequestParam String[] grade, @RequestParam String[] subject, @RequestParam String[] task, @RequestParam String[] category, @RequestParam String[] topic) {
        // Decode URL-encoded parameter values
        try {
            for (int i = 0; i < grade.length; i++) {
                grade[i] = URLDecoder.decode(grade[i], "UTF-8");
            }
            for (int i = 0; i < subject.length; i++) {
                subject[i] = URLDecoder.decode(subject[i], "UTF-8");
            }
            for (int i = 0; i < task.length; i++) {
                task[i] = URLDecoder.decode(task[i], "UTF-8");
            }
            for (int i = 0; i < category.length; i++) {
                category[i] = URLDecoder.decode(category[i], "UTF-8");
            }
            for (int i = 0; i < topic.length; i++) {
                topic[i] = URLDecoder.decode(topic[i], "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            // Handle the exception appropriately
            return Result.fail("Error decoding parameters");
        }

        return questionService.getLabel(grade, subject, task, category, topic);
    }

    @GetMapping("/getskill")
    public Result getskill(){
        return questionService.getQuestionSkill();
    }


}
