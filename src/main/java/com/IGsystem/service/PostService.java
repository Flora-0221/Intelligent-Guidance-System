package com.IGsystem.service;

import com.IGsystem.dto.PostDTO;
import com.IGsystem.dto.Result;
import com.IGsystem.entity.Comment;

public interface PostService {
    public Result getAllPosts();
    public Result getAllTopics();
    public Result getPostById(Long id);
    public Result createPost(PostDTO postDTO);
    public Result addComment(Long postId, Comment comment);
    public Result likePost(Long postId);
    public Result searchPosts(String keyword);
    public Result likeComment(Long postId, Long commentId);
    public Result addNestedComment(Long postId, Long parentCommentId, Comment comment);
    public Result getComments(Long postId);
}
