package com.IGsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class TextQuestion {
    private String question;
    private List<String> choices;
    private int answer;
    private String solution;
    private String hint;
    private String grade;
    private String subject;
    private String topic;
    private String category;
    private String skill;
    private String lecture;
}
