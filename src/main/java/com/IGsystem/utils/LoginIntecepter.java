package com.IGsystem.utils;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class LoginIntecepter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断是否需要拦截 (Thread Local 中是否有用户)
        if(UserHolder.getUser() == null){
            response.setStatus(401);
            return false;
        }
        //有用户放行
        return true;
    }


}
