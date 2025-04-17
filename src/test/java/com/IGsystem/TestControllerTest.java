package com.IGsystem;

import com.IGsystem.controller.TestController;
import com.IGsystem.dto.GradeRequest;
import com.IGsystem.dto.Result;
import com.IGsystem.dto.SAGradeRequest;
import com.IGsystem.dto.UserDTO;
import com.IGsystem.entity.TestResult;
import com.IGsystem.service.TestService;
import com.IGsystem.utils.UserHolder;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TestControllerTest {

    @Mock
    private TestService testService;

    @Mock
    private UserHolder userHolder;

    @InjectMocks
    private TestController testController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        // 使用 MockMvc 进行单元测试
        mockMvc = MockMvcBuilders.standaloneSetup(testController).build();
    }

    @Test
    public void testGetGrade() throws Exception {
        // 模拟服务层返回数据
        GradeRequest gradeRequest = new GradeRequest();
        when(testService.getGrade(gradeRequest)).thenReturn(Result.ok("Mock Grade"));

        // 执行 POST 请求并验证响应
        mockMvc.perform(post("/test/getGrade")
                        .contentType("application/json")
                        .content("{\"field1\": \"value1\", \"field2\": \"value2\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Mock Grade"));
    }

    @Test
    public void testGetTest() throws Exception {
        // 模拟服务层返回数据
        when(testService.getTest(1L)).thenReturn(Result.ok("Mock Test"));

        // 执行 GET 请求并验证响应
        mockMvc.perform(get("/test/getTest")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Mock Test"));
    }

    @Test
    public void testGetSAGrade() throws Exception {
        // 模拟服务层返回数据
        SAGradeRequest saGradeRequest = new SAGradeRequest();
        when(testService.getSAGrade(saGradeRequest)).thenReturn(Result.ok("Mock SA Grade"));

        // 执行 POST 请求并验证响应
        mockMvc.perform(post("/test/getSAGrade/getSAGradeget")
                        .contentType("application/json")
                        .content("{\"field1\": \"value1\", \"field2\": \"value2\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Mock SA Grade"));
    }

    @Test
    public void testGetExplain() throws Exception {
        // 模拟服务层返回数据
        when(testService.getExplain(1)).thenReturn(Result.ok("Mock Explanation"));

        // 执行 GET 请求并验证响应
        mockMvc.perform(get("/test/getExplain")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Mock Explanation"));
    }

    @Test
    public void testOptimizeExplain() throws Exception {
        // 模拟服务层返回数据
        when(testService.optimizeExplain(1, "Mock feedback")).thenReturn(Result.ok("Mock Optimized Explanation"));

        // 执行 GET 请求并验证响应
        mockMvc.perform(get("/test/optimizeExplain")
                        .param("id", "1")
                        .param("userFeedback", "Mock feedback"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Mock Optimized Explanation"));
    }



    @Test
    public void testDeleteTest() throws Exception {
        // 模拟服务层删除数据
        TestResult testResult = new TestResult();
        when(testService.deleteTest(testResult)).thenReturn(Result.ok());

        // 执行 POST 请求并验证响应
        mockMvc.perform(post("/test/delete")
                        .contentType("application/json")
                        .content("{\"field1\": \"value1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
