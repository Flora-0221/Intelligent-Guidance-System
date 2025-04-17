package com.IGsystem.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
public class PostTopic {
    private Long postId;
    private Long topicId;
}
