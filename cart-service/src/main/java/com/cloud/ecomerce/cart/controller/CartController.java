package com.cloud.ecomerce.cart.controller;

import com.cloud.ecomerce.cart.client.ProductClient;
import com.cloud.ecomerce.cart.model.Cart;
import com.cloud.ecomerce.cart.model.CartItem;
import com.cloud.ecomerce.cart.model.CartStatus;
import com.cloud.ecomerce.cart.model.Product;
import com.cloud.ecomerce.cart.repository.CartItemRepository;
import com.cloud.ecomerce.cart.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@RestController
@EnableResourceServer
public class CartController extends ResourceServerConfigurerAdapter {

    @Autowired
    CartRepository cartRepository;
    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ProductClient productClient;

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";

    /**
     * Open a new shopping cart
     * <p>
     *     The cart will be tied to the user by it's name (taken from the authentication)
     *     If there is already an OPEN cart for this user it will return it's id
     * </p>
     * @return the opened cart's id
     */
    @Secured({ROLE_USER})
    @PostMapping("/")
    public Long openCart(){
        List<Cart> cartList = cartRepository.findByOwner(SecurityContextHolder.getContext().getAuthentication().getName());
        //Don't open a new cart if an open cart already exists for this client
        if(!cartList.isEmpty()){
            Optional<Cart> cartOptional = cartList.stream().filter(cart -> cart.getStatus().equals(CartStatus.OPEN)).findFirst();
            if(cartOptional.isPresent())
            return cartOptional.get().getId();
        }
        Cart cart = new Cart();
        cart.setOwner(SecurityContextHolder.getContext().getAuthentication().getName());
        cart.setStatus(CartStatus.OPEN);
        cartRepository.save(cart);
        return cart.getId();
    }

    /**
     * get a cart by its id
     * @param id the cart's id
     * @return an optional of cart
     */
    @Secured({ROLE_USER})
    @GetMapping("/id/{id}")
    public Optional<Cart> getCart(@PathVariable("id") Long id){
        return cartRepository.findById(id);
    }


    /**
     * get a cart with all its products by its id
     * <p>
     *     This method will fill the cart with all its products before returning
     * </p>
     * @param cartId the cart's id
     * @return an optional of cart
     */
    @Secured({ROLE_USER})
    @GetMapping("/products/{id}")
    public Optional<Cart> getCartWithProducts(@PathVariable("id") Long cartId){
        Optional<Cart> cartOptional =  cartRepository.findById(cartId);
        if(cartOptional.isPresent()){
            Cart cart = cartOptional.get();
            for(CartItem item:cart.getItems()){
                Optional<Product> product = productClient.findProductById(getToken(),item.getProductId());
                product.ifPresent(item::setProduct);
            }
            return Optional.of(cart);
        }
        return Optional.empty();
    }

    /**
     * Add a product to the shopping cart
     * @param cartId the cart's id
     * @param newItem the new item to add
     * @return ResponseEntity.ok if the product was added or modified ResponseEntity.badRequest if the cart was not found or was closed
     */
    @Secured({ROLE_USER})
    @PostMapping("/{cart}")
    public ResponseEntity<?> addProduct(@PathVariable("cart") Long cartId,@Valid @RequestBody CartItem newItem) {
        Optional<Cart> cartOptional = cartRepository.findById(cartId);
        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();
            if (cart.getStatus() == CartStatus.OPEN) {
                String prefix = "Added";
                Optional<CartItem> cartItem = cart.getItems().stream().filter(item -> item.getProductId().equals(newItem.getProductId())).findAny();
                if (cartItem.isPresent()) {
                    //If the item already exists in the cart overwrite it (delete the previous item)
                    cartRepository.deleteItem(cartItem.get().getId());
                    cartItemRepository.delete(cartItem.get());
                    prefix = "Modified";
                }
                if (productClient.findProductById(getToken(), newItem.getProductId()).isPresent()) {
                    cartItemRepository.save(newItem);
                    cartRepository.addCartItem(cart.getId(), newItem.getId());
                    return ResponseEntity.ok(prefix + " product (cartItemId = " + newItem.getId() + ")");
                }
            }
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Delete a product from the shopping cart by its id
     * @param cartId the cart's id
     * @param itemId the item's id
     * @return ok if the product was deleted notFound if either the cart or the item couldn't be found
     */
    @Secured({ROLE_USER})
    @DeleteMapping("/{cart}")
    public ResponseEntity<?> deleteProduct(@PathVariable("cart") Long cartId, Long itemId){
            Optional<Cart> cartOptional = cartRepository.findById(cartId);
            if(cartOptional.isPresent()) {
                Cart cart = cartOptional.get();
                if (cart.getStatus() == CartStatus.OPEN) {
                    Optional<CartItem> cartItem = cart.getItems().stream().filter(item -> item.getProductId().equals(itemId)).findAny();
                    if (cartItem.isPresent()) {
                        //If the item already exists in the cart overwrite it (delete the previous item)
                        cartItemRepository.delete(cartItem.get());
                        cartRepository.deleteItem(cartItem.get().getId());
                        return ResponseEntity.ok("Deleted product " + itemId);
                    }
                }
            }
            return ResponseEntity.notFound().build();
    }

    /**
     * Close the shopping cart
     * <p>
     *     The shopping cart needs to be closed in order for the orders service to be able use it to checkout.
     *     Once closed the cart can't be opened and products can no longer be added.
     *     in order for the cart to be closed the request has to be made by it's owner
     * </p>
     * @param cartId the cart's id
     * @return ok if the cart could be closed, badRequest if it couldn't
     */
    @Secured({ROLE_USER})
    @PostMapping("/close/{id}")
    public ResponseEntity<?> close(@PathVariable("id") Long cartId){
        Optional<Cart> cartOptional = cartRepository.findById(cartId);
        if(cartOptional.isPresent()){
            Cart cart = cartOptional.get();
            if(cart.getStatus() == CartStatus.OPEN && cart.getOwner().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
                cart.setStatus(CartStatus.CLOSED);
                cartRepository.save(cart);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    private String getToken(){
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return "Bearer " + details.getTokenValue();
    }

    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers("/actuator","/actuator/**","/v2/api-docs").permitAll()
                .anyRequest().authenticated();
    }
}
