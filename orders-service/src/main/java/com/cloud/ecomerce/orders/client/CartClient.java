package com.cloud.ecomerce.orders.client;

import com.cloud.ecomerce.orders.model.Cart;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;

@FeignClient(name = "cart-service")
public interface CartClient {

    @GetMapping("/id/{id}")
    Optional<Cart> getCartById(@RequestHeader("Authorization") String bearerToken, @PathVariable("id") Long id);

    @GetMapping("/products/{id}")
    Optional<Cart> getCartWithProducts(@RequestHeader("Authorization") String bearerToken,@PathVariable("id") Long id);
}
