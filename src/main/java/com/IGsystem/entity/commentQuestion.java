package com.IGsystem.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class commentQuestion {
    @Id
    private Long id;
    private String content;
    private Long questionId;
    private Long authorId;
    private Long parentCommentId; // 父级评论ID
    private int likeCount;
    private String createdAt;
}
