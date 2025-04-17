package com.IGsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserQuestionDTO {
    private String id;
    private String title;
    private String content;
    private int viewCount;
    private int likeCount;
    private Long authorId;
    private List<String> topics;
    private String createdAt;
}
