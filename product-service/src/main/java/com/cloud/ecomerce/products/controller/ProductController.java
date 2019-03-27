package com.cloud.ecomerce.products.controller;

import com.cloud.ecomerce.products.model.Product;
import com.cloud.ecomerce.products.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@EnableResourceServer
public class ProductController extends ResourceServerConfigurerAdapter {
    @Autowired
    ProductRepository productRepository;
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";


    /**
     * Get a product from its id
     * @param id The id of the product to get
     * @return Optional of the product (empty when not found)
     */
    @GetMapping("/id/{id}")
    public Optional<Product> findProductById(@PathVariable(value = "id") Long id){
        return productRepository.findById(id);
    }


    /**
     * Get all products
     * @return A list of products
     */
    @GetMapping("/all")
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    /**
     * Get all products with the category specified
     * @param category Category to search for
     * @return
     */
    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable(value = "category") String category){
        return productRepository.findByCategory(category);
    }

    /**
     * Add a product to the database
     * JSON example: {"name" : "Tostadora" ,"description" : "La mejor tostadora","category" : "Electrodomesticos","price" : 80, "manufacturer" : "Tostadoras Genericas S.A"}
     * @param product A valid product
     * @return
     */
    @Secured({ROLE_ADMIN})
    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@Valid @RequestBody Product product){
        if(!productRepository.findByName(product.getName()).isPresent()) {
            product.setSeller(SecurityContextHolder.getContext().getAuthentication().getName());
            productRepository.save(product);
            return ResponseEntity.ok().build();
        }
        else
            return ResponseEntity.badRequest().build();
    }

    /**
     * Delete a product by its name
     * @param productName name of the product to delete
     * @return
     */
    @Secured({ROLE_ADMIN})
    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteProduct(@PathVariable(value = "name") String productName){
        return deleteOptional(productRepository.findByName(productName));
    }
    /**
     * Delete a product by its id
     * @param id id of the product to delete
     * @return
     */
    @Secured({ROLE_ADMIN})
    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable(value = "id") Long id){
        return deleteOptional(productRepository.findById(id));
    }

    private ResponseEntity<?> deleteOptional(Optional<Product> product){
        if(product.isPresent()){
            productRepository.delete(product.get());
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers("/id/**","/actuator/**","/actuator","/v2/api-docs").permitAll()
                .anyRequest().authenticated();
    }
}
