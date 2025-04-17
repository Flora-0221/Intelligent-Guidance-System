package com.IGsystem;

import com.IGsystem.controller.QuestionController;
import com.IGsystem.dto.Result;
import com.IGsystem.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class QuestionControllerTest {

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private QuestionController questionController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        // 使用 MockMvc 进行单元测试
        mockMvc = MockMvcBuilders.standaloneSetup(questionController).build();
    }

    @Test
    public void testGetQuestion() throws Exception {
        // 模拟服务层返回数据
        when(questionService.get()).thenReturn(Result.ok("Mock Question"));

        // 执行 GET 请求并验证响应
        mockMvc.perform(get("/question/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Mock Question"));
    }

    @Test
    public void testGetImage() throws Exception {
        // 模拟服务层返回数据
        when(questionService.getImage("split", 1L)).thenReturn(Result.ok("Mock Image"));

        // 执行 GET 请求并验证响应
        mockMvc.perform(get("/question/getImage")
                        .param("split", "split")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Mock Image"));
    }

    @Test
    public void testGetQuestionByID() throws Exception {
        // 模拟服务层返回数据
        when(questionService.getQuestionByID("1,2")).thenReturn(Result.ok("Mock Question by ID"));

        // 执行 GET 请求并验证响应
        mockMvc.perform(get("/question/getQuestionByID")
                        .param("questionids", "1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Mock Question by ID"));
    }

    @Test
    public void testGetQuestionWithFilters() throws Exception {
        // 模拟服务层返回数据
        when(questionService.getQuestion("task1", "grade1", "subject1", "topic1", "category1", 10))
                .thenReturn(Result.ok("Mock Filtered Question"));

        // 执行 GET 请求并验证响应
        mockMvc.perform(get("/question/getQuestion")
                        .param("task", "task1")
                        .param("grade", "grade1")
                        .param("subject", "subject1")
                        .param("topic", "topic1")
                        .param("category", "category1")
                        .param("questionCount", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Mock Filtered Question"));
    }

    @Test
    public void testGetFilteredQuestion() throws Exception {
        // 模拟服务层返回数据
        when(questionService.getByLabel("grade1", "subject1", "task1", "category1", "topic1"))
                .thenReturn(Result.ok("Mock Filtered Question"));

        // 执行 GET 请求并验证响应
        mockMvc.perform(get("/question/getQuestionByLabel")
                        .param("grade", "grade1")
                        .param("subject", "subject1")
                        .param("task", "task1")
                        .param("category", "category1")
                        .param("topic", "topic1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Mock Filtered Question"));
    }

    @Test
    public void testGetFilteredOptions() throws Exception {
        // 模拟服务层返回数据
        when(questionService.getLabel(new String[]{"grade1"}, new String[]{"subject1"}, new String[]{"task1"}, new String[]{"category1"}, new String[]{"topic1"}))
                .thenReturn(Result.ok("Mock Filtered Options"));

        // 执行 GET 请求并验证响应
        mockMvc.perform(get("/question/getOption")
                        .param("grade", "grade1")
                        .param("subject", "subject1")
                        .param("task", "task1")
                        .param("category", "category1")
                        .param("topic", "topic1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Mock Filtered Options"));
    }

    @Test
    public void testGetSkill() throws Exception {
        // 模拟服务层返回数据
        when(questionService.getQuestionSkill()).thenReturn(Result.ok("Mock Skill"));

        // 执行 GET 请求并验证响应
        mockMvc.perform(get("/question/getskill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Mock Skill"));
    }
}
