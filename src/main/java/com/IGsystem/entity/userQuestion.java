package com.IGsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class userQuestion {
    private Long id;
    private String title;
    private String content;
    private int viewCount;
    private int likeCount;
    private Long authorId;
    private String createdAt;
}
