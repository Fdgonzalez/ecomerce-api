package com.cloud.ecomerce.cart.repository;

import com.cloud.ecomerce.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Integer> {
    List<Cart> findByOwner(String owner);
    Optional<Cart> findById(Long id);

    @Modifying
    @Transactional
    @Query(value = "insert into cart_cart_items (cart_id, cart_item_id) values (:cartId, :cartItemId)",
            nativeQuery = true)
    void addCartItem(@Param("cartId") Long cart, @Param("cartItemId") Long item);

    @Modifying
    @Transactional
    @Query(value = "delete from cart_cart_items where cart_item_id = :id",nativeQuery = true)
    void deleteItem(@Param("id") Long id);
}
