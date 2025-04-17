package com.IGsystem.service;

import com.IGsystem.dto.LoginFormDTO;
import com.IGsystem.dto.RegisterFormDTO;
import com.IGsystem.dto.Result;
import com.IGsystem.dto.UserDTO;

import javax.servlet.http.HttpSession;


/**
 * 服务类
 */
public interface UserService {
    Result login(LoginFormDTO loginForm, HttpSession session);
    Result register(RegisterFormDTO registerFormDTO, HttpSession session);
    Result findUserByName(String username);
    Result updateUser (UserDTO user);

}
