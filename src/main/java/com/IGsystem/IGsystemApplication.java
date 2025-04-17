package com.IGsystem;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


@MapperScan("com.IGsystem.mapper")
@SpringBootApplication
@Slf4j
@ServletComponentScan
public class IGsystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(IGsystemApplication.class,args);
        log.info("项目启动成功......");
    }
}

