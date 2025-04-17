package com.IGsystem.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName("posts")
public class PostDTO {
    private String id;
    private String title;
    private String content;
    private int viewCount;
    private int likeCount;
    private Long authorId;
    private List<String> topics;
    private String createdAt;
}
