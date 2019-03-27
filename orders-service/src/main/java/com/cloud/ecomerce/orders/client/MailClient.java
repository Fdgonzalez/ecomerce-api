package com.cloud.ecomerce.orders.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "email-service")
public interface MailClient {
    @GetMapping("/mail/{id}")
    void mailOrder(@RequestHeader("Authorization") String bearerToken, @PathVariable("id") Long id);
}
