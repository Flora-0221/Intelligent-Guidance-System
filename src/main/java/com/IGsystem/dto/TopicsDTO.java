package com.IGsystem.dto;

import com.IGsystem.entity.Topics;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class TopicsDTO {
    private String id;
    private String name;

    public TopicsDTO(Topics topic) {
        this.id = String.valueOf(topic.getId());
        this.name = topic.getName();
    }
}
