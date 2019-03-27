package com.cloud.ecomerce.cart.repository;

import com.cloud.ecomerce.cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem,Integer> {
}
