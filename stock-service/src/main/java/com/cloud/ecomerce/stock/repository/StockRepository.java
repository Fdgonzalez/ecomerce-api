package com.cloud.ecomerce.stock.repository;

import com.cloud.ecomerce.stock.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Integer> {
    Optional<Stock> findByProductId(Long id);
}
