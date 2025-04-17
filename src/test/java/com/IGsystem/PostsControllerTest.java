package com.IGsystem;

import com.IGsystem.controller.PostsController;
import com.IGsystem.dto.Result;
import com.IGsystem.entity.Comment;
import com.IGsystem.entity.Post;
import com.IGsystem.service.Imp.PostServiceImp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
public class PostsControllerTest {

    @Mock
    private PostServiceImp postService;

    @InjectMocks
    private PostsController postsController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(postsController).build();
    }

    @Test
    public void testGetAllPosts() throws Exception {
        // Mocking the service method
        when(postService.getAllPosts()).thenReturn(Result.ok());

        mockMvc.perform(MockMvcRequestBuilders.get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testGetPostById() throws Exception {
        // Mocking the service method
        when(postService.getPostById(1L)).thenReturn(Result.ok());

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testCreatePost() throws Exception {
        // Prepare the request body
        String postJson = "{\"title\": \"New Post\", \"content\": \"This is the content\"}";

        // Mocking the service method
        when(postService.createPost(any())).thenReturn(Result.ok());

        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testAddComment() {
        // 创建测试数据
        Long postId = 1L;
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("This is a comment");

        // 配置 mock
        when(postService.addComment(anyLong(), eq(comment))).thenReturn(Result.ok());

        // 执行测试
        Result result = postsController.addComment(postId, comment);

        // 验证
        assertNotNull(result);
        // 假设 isOk() 返回的是 "true" 或 "false"
        assertEquals(true, result.isOk());  // 判断返回值是否是 "true"
    }



    @Test
    public void testLikePost() throws Exception {
        // Mocking the service method
        when(postService.likePost(1L)).thenReturn(Result.ok());

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/{id}/like", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
