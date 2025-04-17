package com.IGsystem.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RegisterFormDTO {
    private String phone;
    private String password;
    private String nickName;
    private String gender;
    private String occupation;
    private String email;
    private Date birthdate;
    private int age;
}
