package com.IGsystem.dto;

import com.IGsystem.entity.TestResult;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SATestResultDTO extends TestResult {
    private List<SAQuestion> questions;
    private List<Double> scoreList;

}
