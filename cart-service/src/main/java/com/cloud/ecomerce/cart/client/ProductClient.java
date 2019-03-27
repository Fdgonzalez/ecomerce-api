package com.cloud.ecomerce.cart.client;

import com.cloud.ecomerce.cart.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;


@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/id/{id}")
    Optional<Product> findProductById(@RequestHeader("Authorization") String bearerToken, @PathVariable("id") Long id);
}
