package com.IGsystem.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;

@Data
@Slf4j
public class LikesQuestion {
    @Id
    private Long id;
    private Long questionId;
    private Long userId;
    private String createdAt;
}
