package com.IGsystem.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class Topics {
    @Id
    private Long id;
    private String name;
}
