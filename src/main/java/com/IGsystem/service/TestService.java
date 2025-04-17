package com.IGsystem.service;


import com.IGsystem.dto.GradeRequest;
import com.IGsystem.dto.Result;
import com.IGsystem.dto.SAGradeRequest;
import com.IGsystem.dto.TextQuestion;
import com.IGsystem.entity.TestResult;
import com.baomidou.mybatisplus.extension.service.IService;

public interface TestService extends IService<TestResult> {
    Result getTest(Long id);
    Result saveResult(TestResult testResult);
    Result getGrade(GradeRequest gradeRequest);
    Result getSAGrade(SAGradeRequest saGradeRequest);
    Result getExplain(int id);
    Result optimizeExplain(int id,String userFeedback);
    Result deleteTest(TestResult testResult);
}
