package com.IGsystem.controller;

import com.IGsystem.dto.PostDTO;
import com.IGsystem.dto.Result;
import com.IGsystem.dto.UserQuestionDTO;
import com.IGsystem.entity.Comment;
import com.IGsystem.entity.commentQuestion;
import com.IGsystem.service.PostService;
import com.IGsystem.service.UserQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userQuestion")
@CrossOrigin(origins = "*") // 允许所有域名的请求
@Slf4j
public class UserQuestionController {
    @Autowired
    private UserQuestionService questionService;

    /**
     * 获取所有的问答
     * @return 统一返回体
     */
    @GetMapping
    public Result getAllPosts() {
        return questionService.getAllQuestions();
    }

    @GetMapping("/topics")
    public Result getAllTopics() {
        return questionService.getAllTopics();
    }

    /**
     * 根据id获取问答
     * @param id 用户id
     * @return 统一返回体
     */
    @GetMapping("/{id}")
    public Result getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }

    /**
     * 新键一个问答
     * @param questionDTO 问答
     * @return 统一返回体
     */
    @PostMapping
    public Result createPost(@RequestBody UserQuestionDTO questionDTO) {
        return questionService.createQuestion(questionDTO);
    }

    /**
     * 添加一个评论
     * @param id 用户的id
     * @param comment 添加的评论
     * @return 返回统一请求体
     */
    @PostMapping("/{id}/comments")
    public Result addComment(@PathVariable Long id, @RequestBody commentQuestion comment) {
        return questionService.addComment(id, comment);
    }
    /**
     * 点赞评论
     * @param questionId 问答的id
     * @param commentId 评论的id
     * @return 返回统一请求体
     */
    @GetMapping("/{questionId}/comments/{commentId}/like")
    public Result likeComment(@PathVariable Long questionId, @PathVariable Long commentId) {
        return questionService.likeComment(questionId, commentId);
    }

    /**
     * 添加一个嵌套评论
     * @param questionId 问答的id
     * @param parentCommentId 父级评论的id，如果是顶级评论，这里可以为空
     * @param comment 添加的评论
     * @return 返回统一请求体
     */
    @PostMapping("/{questionId}/comments/{parentCommentId}/nested")
    public Result addNestedComment(@PathVariable Long questionId,
                                   @PathVariable(required = false) Long parentCommentId,
                                   @RequestBody commentQuestion comment) {
        return questionService.addNestedComment(questionId, parentCommentId, comment);
    }

    /**
     * 点赞问答
     * @param id 用户id
     */
    @GetMapping("/{id}/like")
    public Result likePost(@PathVariable Long id) {
        return questionService.likeQuestion(id);
    }

    /**
     * 根据关键词查找问答
     * @param keyword 关键词
     * @return 统一请求体
     */
    @GetMapping("/search")
    public Result searchPosts(@RequestParam String keyword) {
        return questionService.searchQuestions(keyword);
    }

    /**
     * 获取该问答下的所有评论
     * @param questionId 问答的id
     * @return 返回统一请求体
     */
    @GetMapping("/{questionId}/comments")
    public Result likeComment(@PathVariable Long questionId) {
        return questionService.getComments(questionId);
    }
}
