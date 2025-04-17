package com.IGsystem.entity;

import com.IGsystem.dto.Gender;
import com.IGsystem.dto.Occupation;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;


@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 密码，加密存储
     */
    private String password;

    /**
     * 昵称，默认是随机字符
     */
    private String nickName;

    /**
     * 用户头像
     */
    private String icon = "";

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别
     */
    private Gender gender;

    /**
     * 国家
     */
    private String country;

    /**
     * 地址
     */
    private String address;

    /**
     * 职业
     */
    private Occupation occupation;

    /**
     * 生日
     */
    private Date birthdate;

    /**
     * 年龄
     */
    private int age;

    /**
     * 个性签名
     */
    private String comment;

}
