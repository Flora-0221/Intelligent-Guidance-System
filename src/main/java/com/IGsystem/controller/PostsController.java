package com.IGsystem.controller;

import com.IGsystem.dto.PostDTO;
import com.IGsystem.dto.Result;
import com.IGsystem.entity.Comment;
import com.IGsystem.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*") // 允许所有域名的请求
@Slf4j
public class PostsController {
    @Autowired
    private PostService postService;

    /**
     * 获取所有的帖子
     * @return 统一返回体
     */
    @GetMapping
    public Result getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/topics")
    public Result getAllTopics() {
        return postService.getAllTopics();
    }

    /**
     * 根据id获取帖子
     * @param id 用户id
     * @return 统一返回体
     */
    @GetMapping("/{id}")
    public Result getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    /**
     * 新键一个帖子
     * @param postDTO 帖子
     * @return 统一返回体
     */
    @PostMapping
    public Result createPost(@RequestBody PostDTO postDTO) {
        return postService.createPost(postDTO);
    }

    /**
     * 添加一个评论
     * @param id 用户的id
     * @param comment 添加的评论
     * @return 返回统一请求体
     */
    @PostMapping("/{id}/comments")
    public Result addComment(@PathVariable Long id, @RequestBody Comment comment) {
        return postService.addComment(id, comment);
    }
    /**
     * 点赞评论
     * @param postId 帖子的id
     * @param commentId 评论的id
     * @return 返回统一请求体
     */
    @GetMapping("/{postId}/comments/{commentId}/like")
    public Result likeComment(@PathVariable Long postId, @PathVariable Long commentId) {
        return postService.likeComment(postId, commentId);
    }

    /**
     * 添加一个嵌套评论
     * @param postId 帖子的id
     * @param parentCommentId 父级评论的id，如果是顶级评论，这里可以为空
     * @param comment 添加的评论
     * @return 返回统一请求体
     */
    @PostMapping("/{postId}/comments/{parentCommentId}/nested")
    public Result addNestedComment(@PathVariable Long postId,
                                   @PathVariable(required = false) Long parentCommentId,
                                   @RequestBody Comment comment) {
        return postService.addNestedComment(postId, parentCommentId, comment);
    }

    /**
     * 点赞帖子
     * @param id 用户id
     */
    @GetMapping("/{id}/like")
    public Result likePost(@PathVariable Long id) {
        return postService.likePost(id);
    }

    /**
     * 根据关键词查找帖子
     * @param keyword 关键词
     * @return 统一请求体
     */
    @GetMapping("/search")
    public Result searchPosts(@RequestParam String keyword) {
        return postService.searchPosts(keyword);
    }

    /**
     * 获取该帖子下的所有评论
     * @param postId 帖子的id
     * @return 返回统一请求体
     */
    @GetMapping("/{postId}/comments")
    public Result likeComment(@PathVariable Long postId) {
        return postService.getComments(postId);
    }

}
