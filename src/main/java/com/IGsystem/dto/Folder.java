package com.IGsystem.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("favorites")
public class Folder {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    private String name;

    private String description;

    private Date createdAt;

    private Long userId;
}
