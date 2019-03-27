package com.cloud.ecomerce.mailer.model;

public class Order {

    private Long id;

    private Long cartId;

    private OrderStatus status;

    private Cart cart;

    private String owner;

    private float total;

    @Override
    public String toString(){
        StringBuilder products = new StringBuilder();
        for(CartItem item : cart.getItems()){
            products.append(item.getProduct().getName());
            products.append("\n");
        }
        return "ID: " + id + "\n" +
                "Status: " + status.name() + "\n" +
                "Total: " + total + "\n" +
                "Products : "+ "\n" +
                "------------------------"
                +products;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
