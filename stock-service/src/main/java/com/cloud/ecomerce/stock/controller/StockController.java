package com.cloud.ecomerce.stock.controller;

import com.cloud.ecomerce.stock.client.OrderClient;
import com.cloud.ecomerce.stock.model.CartItem;
import com.cloud.ecomerce.stock.model.Order;
import com.cloud.ecomerce.stock.model.Stock;
import com.cloud.ecomerce.stock.repository.StockRepository;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@EnableResourceServer
public class StockController extends ResourceServerConfigurerAdapter {
    @Autowired
    StockRepository stockRepository;

    @Autowired
    OrderClient orderClient;

    private static final String ROLE_USER = "ROLE_USER";

    @Secured({ROLE_USER})
    @GetMapping("/{id}")
    public ResponseEntity<Long> getStockForId(@PathVariable(name = "id") Long id){
        Optional<Stock> stockOptional = stockRepository.findByProductId(id);
        return stockOptional.map(stock -> ResponseEntity.ok(stock.getQuantity())).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @Secured({ROLE_USER})
    @GetMapping("/add/{id}/{qty}")
    public ResponseEntity<Long> addStockForId(@PathVariable(name = "id") Long id,@PathVariable(name = "qty") Long qty){
        Optional<Stock> stockOptional = stockRepository.findByProductId(id);
        Stock stock;
        if(stockOptional.isPresent()){
            stock = stockOptional.get();
            stock.setQuantity(stock.getQuantity() + qty);
        }else {
            stock = new Stock(id, qty);
        }
        stockRepository.save(stock);
        return ResponseEntity.ok(stock.getQuantity());
    }

    @Secured({ROLE_USER})
    @GetMapping("/order/{id}")
    public ResponseEntity<String> checkOrder(@PathVariable(name = "id") Long orderId){
        Optional<Order> orderOptional = orderClient.getOrderWithProducts(getToken(),orderId);
        if(orderOptional.isPresent()){
            StringBuilder statusMessage = new StringBuilder();
            boolean failed = false;
            for(CartItem item : orderOptional.get().getCart().getItems()){
                Optional<Stock> stock = stockRepository.findByProductId(item.getProductId());
                if(stock.isPresent()){
                    if(stock.get().getQuantity() < item.getQuantity()) {
                        statusMessage.append("Stock not enough for product ").append(item.getProductId()).append("\n");
                        failed = true;
                    }
                }else{
                    statusMessage.append("Product not found ").append(item.getProductId()).append("\n");
                }
            }
            if(failed)
                return ResponseEntity.ok(statusMessage.toString());
            for(CartItem item : orderOptional.get().getCart().getItems()){
                Optional<Stock> stock = stockRepository.findByProductId(item.getProductId());
                if(!stock.isPresent())
                    return ResponseEntity.ok("Product disappeared magically");
                stock.get().setQuantity(stock.get().getQuantity() - item.getQuantity());
                stockRepository.save(stock.get());
            }
            return ResponseEntity.ok("Order ready");
        }
        return ResponseEntity.notFound().build();
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
