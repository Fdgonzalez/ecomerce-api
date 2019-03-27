package com.cloud.ecomerce.mailer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/mail/{name}")
    String getUserEmail(@RequestHeader("Authorization") String bearerToken, @PathVariable("name") String name);
}
