package com.IGsystem.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("favorite_questions")
public class FavoriteQuestions {

    private Integer favoriteId;

    private Integer questionId;

    private String questionType;

    private Long userId;
}
