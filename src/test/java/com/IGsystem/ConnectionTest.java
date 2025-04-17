package com.IGsystem;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootTest
public class ConnectionTest {
    @Autowired  //自动注入/自动装配
    private DataSource dataSource;

    @Test
    void contextLoads() {

    }

    @Test
    void getConnection() throws SQLException {
        System.out.println(dataSource.getConnection());
    }

}
