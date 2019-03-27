package com.cloud.ecomerce.stock.client;

import com.cloud.ecomerce.stock.model.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;

@FeignClient(name = "orders-service")
public interface OrderClient {
    @GetMapping("{id}")
    Optional<Order> getOrderWithProducts(@RequestHeader("Authorization") String bearerToken, @PathVariable("id") Long id);
}
