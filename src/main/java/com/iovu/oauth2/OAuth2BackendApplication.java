package com.iovu.oauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.ComponentScan;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan({"com.iovu.oauth2", "com.iovu.iovuback"})
@MapperScan({"com.iovu.iovuback.mapper", "com.iovu.oauth2.mapper"})
@EnableScheduling
public class OAuth2BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OAuth2BackendApplication.class, args);
    }

}
