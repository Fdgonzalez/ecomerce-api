package com.cloud.ecomerce.mailer.controller;

import com.cloud.ecomerce.mailer.client.OrderClient;
import com.cloud.ecomerce.mailer.client.UserClient;
import com.cloud.ecomerce.mailer.model.Order;
import com.cloud.ecomerce.mailer.service.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@EnableResourceServer

public class MailController  extends ResourceServerConfigurerAdapter {
    @Autowired
    OrderClient orderClient;
    @Autowired
    UserClient userClient;

    @Autowired
    EmailServiceImpl mailer;

    @GetMapping("/test")
    public void testEmail(){
        mailer.sendSimpleMessage("facundo915@gmail.com","TEST","This is a test\n, nice test!");
    }

    @PostMapping("/mail/{id}")
    public ResponseEntity<?> mailOrder(@PathVariable(name = "id") Long id){
        Optional<Order> order = orderClient.getOrderWithProducts(getToken(),id);
        order.ifPresent(order1 -> mailer.sendSimpleMessage(userClient.getUserEmail(getToken(), order1.getOwner()), "Your order was confirmed", order1.toString()));
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
