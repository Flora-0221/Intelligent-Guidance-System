package com.IGsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class SAGradeRequest {
//    private List<Integer> questionIds;
//    private List<String> userAnswer;
//    private LocalDateTime startTime;
//    private LocalDateTime finishTime;

    private LocalDateTime finishTime;
    private List<Integer> questionIds;
    private LocalDateTime startTime;
    private Map<Integer, String> userAnswer;
}
