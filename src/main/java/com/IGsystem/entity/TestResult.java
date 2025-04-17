package com.IGsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestResult {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private String questionId;
    private long userId;
    private String testName;
    private String userAnswer;
    private Double userScore;
    private String rightAnswer;
    private long consumingTime;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private int correctNumber;
    private int wrongNumber;
    private String task;
    private String subject;
    private String topic;
    private String category;
    private String explainAi;
    private String scoreForEach;
}
