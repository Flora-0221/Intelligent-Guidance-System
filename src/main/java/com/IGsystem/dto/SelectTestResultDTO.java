package com.IGsystem.dto;

import com.IGsystem.entity.TestResult;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SelectTestResultDTO extends TestResult {
    private List<Question> questions;
    private List<Double> scoreList;
}
