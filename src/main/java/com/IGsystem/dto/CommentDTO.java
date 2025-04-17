package com.IGsystem.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class CommentDTO {
    @Id
    private String id;
    private String content;
    private Long postId;
    private Long authorId;
    private String parentCommentId;
    private int likeCount;
    private String createdAt;
}
