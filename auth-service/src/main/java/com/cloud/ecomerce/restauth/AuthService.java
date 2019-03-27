package com.cloud.ecomerce.restauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class AuthService {

    public static void main(String[] args) {
        SpringApplication.run(AuthService.class, args);

    }
}