package com.IGsystem.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("problems")
public class Question {
//    private Long ProblemId;
//    private String problem;
//    private String level;
//    private String type;
//    private String solution;
//    private Integer mathId;

    @TableId(value = "id")
    private int id;
    private String question;
    private String choices;
    private int answer;
    private String hint;
    private String image;
    private String task;
    private String grade;
    private String subject;
    private String topic;
    private String category;
    private String skill;
    private String lecture;
    private String solution;
    private String split;
    private int isFavorited;
}
