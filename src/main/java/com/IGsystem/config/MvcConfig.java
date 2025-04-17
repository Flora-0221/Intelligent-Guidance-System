package com.IGsystem.config;
import com.IGsystem.utils.LoginIntecepter;
import com.IGsystem.utils.RefreshTokenInteceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //登录验证拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginIntecepter())
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/user/validateName",
                        "/user/download",
                        "/question/getImage"
                ).order(1);

        //token刷新拦截器
        registry.addInterceptor(new RefreshTokenInteceptor(stringRedisTemplate)).addPathPatterns("/**").order(0);
    }

}
