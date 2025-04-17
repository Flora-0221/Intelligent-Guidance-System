package com.IGsystem;

import com.IGsystem.controller.UserQuestionController;
import com.IGsystem.dto.Result;
import com.IGsystem.dto.UserQuestionDTO;
import com.IGsystem.entity.commentQuestion;
import com.IGsystem.service.UserQuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UserQuestionControllerTest {

    @InjectMocks
    private UserQuestionController userQuestionController;

    @Mock
    private UserQuestionService userQuestionService;

    private MockMvc mockMvc;

    private UserQuestionDTO questionDTO;
    private commentQuestion comment;

    @BeforeEach
    public void setUp() {
        // Set up sample data
        questionDTO = new UserQuestionDTO();
        questionDTO.setTitle("Sample Question");
        questionDTO.setContent("This is a test question.");

        comment = new commentQuestion();
        comment.setContent("This is a test comment.");
    }

    @Test
    public void testGetAllPosts() throws Exception {
        Result mockResult = Result.ok();
        when(userQuestionService.getAllQuestions()).thenReturn(mockResult);

        mockMvc.perform(get("/userQuestion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userQuestionService, times(1)).getAllQuestions();
    }

    @Test
    public void testGetAllTopics() throws Exception {
        Result mockResult = Result.ok();
        when(userQuestionService.getAllTopics()).thenReturn(mockResult);

        mockMvc.perform(get("/userQuestion/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userQuestionService, times(1)).getAllTopics();
    }

    @Test
    public void testGetQuestionById() throws Exception {
        Result mockResult = Result.ok();
        when(userQuestionService.getQuestionById(1L)).thenReturn(mockResult);

        mockMvc.perform(get("/userQuestion/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userQuestionService, times(1)).getQuestionById(1L);
    }

    @Test
    public void testCreatePost() throws Exception {
        Result mockResult = Result.ok();
        when(userQuestionService.createQuestion(any(UserQuestionDTO.class))).thenReturn(mockResult);

        mockMvc.perform(post("/userQuestion")
                        .contentType("application/json")
                        .content("{\"title\":\"Sample Question\", \"description\":\"This is a test question.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userQuestionService, times(1)).createQuestion(any(UserQuestionDTO.class));
    }

    @Test
    public void testAddComment() throws Exception {
        Result mockResult = Result.ok();
        when(userQuestionService.addComment(eq(1L), any(commentQuestion.class))).thenReturn(mockResult);

        mockMvc.perform(post("/userQuestion/{id}/comments", 1L)
                        .contentType("application/json")
                        .content("{\"content\":\"This is a test comment.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userQuestionService, times(1)).addComment(eq(1L), any(commentQuestion.class));
    }

    @Test
    public void testLikeComment() throws Exception {
        Result mockResult = Result.ok();
        when(userQuestionService.likeComment(eq(1L), eq(1L))).thenReturn(mockResult);

        mockMvc.perform(get("/userQuestion/{questionId}/comments/{commentId}/like", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userQuestionService, times(1)).likeComment(eq(1L), eq(1L));
    }

    @Test
    public void testAddNestedComment() throws Exception {
        Result mockResult = Result.ok();
        when(userQuestionService.addNestedComment(eq(1L), eq(2L), any(commentQuestion.class))).thenReturn(mockResult);

        mockMvc.perform(post("/userQuestion/{questionId}/comments/{parentCommentId}/nested", 1L, 2L)
                        .contentType("application/json")
                        .content("{\"content\":\"This is a nested comment.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userQuestionService, times(1)).addNestedComment(eq(1L), eq(2L), any(commentQuestion.class));
    }

    @Test
    public void testLikePost() throws Exception {
        Result mockResult = Result.ok();
        when(userQuestionService.likeQuestion(eq(1L))).thenReturn(mockResult);

        mockMvc.perform(get("/userQuestion/{id}/like", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userQuestionService, times(1)).likeQuestion(eq(1L));
    }

    @Test
    public void testSearchPosts() throws Exception {
        Result mockResult = Result.ok();
        when(userQuestionService.searchQuestions("test")).thenReturn(mockResult);

        mockMvc.perform(get("/userQuestion/search?keyword=test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userQuestionService, times(1)).searchQuestions("test");
    }

    @Test
    public void testGetComments() throws Exception {
        Result mockResult = Result.ok();
        when(userQuestionService.getComments(eq(1L))).thenReturn(mockResult);

        mockMvc.perform(get("/userQuestion/{questionId}/comments", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userQuestionService, times(1)).getComments(eq(1L));
    }
}
