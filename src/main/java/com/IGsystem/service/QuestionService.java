package com.IGsystem.service;

import com.IGsystem.dto.Result;
import org.springframework.web.bind.annotation.RequestParam;


public interface QuestionService {
    Result get();
    Result getQuestion(String task, String grade, String subject, String topic, String category,int questionCount);
    Result getImage(String split, Long id);
    Result getQuestionByID(String questionids);
    Result getByLabel( String grade,  String subject,  String task, String category,  String topic);
    Result getLabel( String[] grade,  String[] subject,  String[] task, String[] category,  String[] topic);
    Result getQuestionSkill();
}
