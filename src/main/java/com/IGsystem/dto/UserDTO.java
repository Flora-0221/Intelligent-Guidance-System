package com.IGsystem.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class UserDTO {

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;
    private String nickName;
    private String icon;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String email;
    private Gender gender;
    private String country;
    private String address;
    private Occupation occupation;
    private Date birthdate;
    private int age;
    private String comment;
    private String phone;
}
