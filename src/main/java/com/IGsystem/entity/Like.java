package com.IGsystem.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;

@Data
@Slf4j
public class Like {
    @Id
    private Long id;
    private Long postId;
    private Long userId;
}
