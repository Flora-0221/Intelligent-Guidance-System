package com.IGsystem.service;

import com.IGsystem.dto.Result;
import com.IGsystem.dto.UserQuestionDTO;
import com.IGsystem.entity.Comment;
import com.IGsystem.entity.commentQuestion;

public interface UserQuestionService {
    public Result getAllQuestions();
    public Result getAllTopics();
    public Result getQuestionById(Long id);
    public Result createQuestion(UserQuestionDTO questionDTO);
    public Result addComment(Long questionId, commentQuestion comment);
    public Result likeQuestion(Long questionId);
    public Result searchQuestions(String keyword);
    public Result likeComment(Long questionId, Long commentId);
    public Result addNestedComment(Long questionId, Long parentCommentId, commentQuestion comment);
    public Result getComments(Long questionId);
}
