package com.IGsystem.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
public class SAQuestion {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private double Number;
    private String Question;
    private String Answer;
    private int isFavorited;
}
