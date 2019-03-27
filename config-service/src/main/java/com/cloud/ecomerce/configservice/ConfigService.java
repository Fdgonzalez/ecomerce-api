package com.cloud.ecomerce.configservice;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigService {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ConfigService.class).run(args);
    }
}