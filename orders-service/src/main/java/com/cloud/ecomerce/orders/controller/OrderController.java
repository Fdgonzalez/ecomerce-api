package com.cloud.ecomerce.orders.controller;

import com.cloud.ecomerce.orders.client.CartClient;
import com.cloud.ecomerce.orders.client.MailClient;
import com.cloud.ecomerce.orders.model.*;
import com.cloud.ecomerce.orders.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@EnableResourceServer
public class OrderController extends ResourceServerConfigurerAdapter {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CartClient cartClient;
    @Autowired
    MailClient mailer;

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";

    /**
     * Get a list of all orders made by this user
     * the user(name) is taken out of the authentication
     * @return a list of orders
     */
    @Secured({ROLE_USER})
    @GetMapping("/myOrders")
    public List<Order> getUserOrders(){
        return orderRepository.findAllByOwner(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    /**
     * Get an order by its id with all products
     * <p> The request can only be made by the order's owner</p>
     * @param id the order's id
     * @return an optional of order
     */
    @Secured({ROLE_USER})
    @GetMapping("{id}")
    public Optional<Order> getOrderWithProducts(@PathVariable(name="id") Long id){
        Optional<Order> order = orderRepository.findById(id);
        if(order.isPresent() && order.get().getOwner().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            Optional<Cart> cartOptional = cartClient.getCartWithProducts(getToken(),order.get().getCartId());
            cartOptional.ifPresent(cart -> order.get().setCart(cart));
            return order;
        }
        return  Optional.empty();
    }

    /**
     * get and order's status by its id
     * @param orderId the order's id
     * @return the order's status
     */
    @Secured({ROLE_USER})
    @GetMapping("/tracking")
    public ResponseEntity<OrderStatus> track(Long orderId){
        Optional<Order> possibleOrder = orderRepository.findById(orderId);
        return possibleOrder.map(order -> ResponseEntity.ok(order.getStatus())).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Checkout a cart by it's id
     * <p>
     *     The cart should be closed and owned by the person making the request
     *     A cart can't be checked out more than once
     * </p>
     * @param cartId the cart's id
     * @return
     */
    @Secured({ROLE_USER})
    @PostMapping("/checkout/{id}")
    public ResponseEntity<String> checkout(@PathVariable(name = "id") Long cartId){
        if(orderRepository.findByCartId(cartId).isPresent())
            return ResponseEntity.badRequest().body("This cart was already checked out");
        Optional<Cart> cartOptional = cartClient.getCartWithProducts(getToken(),cartId);
        //If the cart doesn't exist or user is not the owner or the cart is open we can't checkout
        if(!cartOptional.isPresent() || !cartOptional.get().getOwner().equals(SecurityContextHolder.getContext().getAuthentication().getName()) || cartOptional.get().getStatus().equals(CartStatus.OPEN))
            return ResponseEntity.badRequest().build();
        Order order = new Order();
        order.setCartId(cartId);
        order.setOwner(cartOptional.get().getOwner());
        float total = 0;
        for(CartItem item : cartOptional.get().getItems()){
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        order.setTotal(total);
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);
        mailer.mailOrder(getToken(),order.getId());
        return ResponseEntity.ok(order.getId().toString());
    }

    private String getToken(){
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return "Bearer " + details.getTokenValue();
    }

    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers("/actuator/","/actuator/**","/v2/api-docs").permitAll()
                .anyRequest().authenticated();
    }
}
