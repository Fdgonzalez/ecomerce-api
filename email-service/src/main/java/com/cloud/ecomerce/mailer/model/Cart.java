package com.cloud.ecomerce.mailer.model;

import java.util.Set;

public class Cart {

    private Long id;

    private String owner;

    private CartStatus status;

    private Set<CartItem> items;

    public Long getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public CartStatus getStatus() {
        return status;
    }

    public Set<CartItem> getItems() {
        return items;
    }
}
