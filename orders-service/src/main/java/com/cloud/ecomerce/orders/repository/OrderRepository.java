package com.cloud.ecomerce.orders.repository;

import com.cloud.ecomerce.orders.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Integer> {
    List<Order> findAllByOwner(String owner);
    Optional<Order> findById(Long id);
    Optional<Order> findByCartId(Long id);
}
