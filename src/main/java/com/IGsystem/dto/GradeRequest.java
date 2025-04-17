package com.IGsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class GradeRequest {
    private List<Integer> questionIds;
    private Map<Integer, Integer> selectedChoices;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private List<String> category;
    private List<String> subject;
    private List<String> task;
    private List<String> topic;
}
