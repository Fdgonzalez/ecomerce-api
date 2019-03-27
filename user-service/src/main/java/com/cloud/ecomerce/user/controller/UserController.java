package com.cloud.ecomerce.user.controller;

import com.cloud.ecomerce.user.model.RoleName;
import com.cloud.ecomerce.user.model.User;
import com.cloud.ecomerce.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@EnableResourceServer
@RestController
public class UserController extends ResourceServerConfigurerAdapter {

    @Autowired
    UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";

    /**
     * Get a user by its username
     * @param username the username to look up
     * @return an optional of User
     */
    @Secured({ROLE_ADMIN})
    @GetMapping("/name/{name}")
    public Optional<User> getUserByUsername(@PathVariable(value = "name") String username){
        return userRepository.findByUsername(username);
    }
    @Secured({ROLE_ADMIN})
    @GetMapping("/all")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    /**
     * Register a user on the database
     * JSON Example:
     * {"username":"USERNAME","password":"PASSWORD","name":"name","lastname":"lastname","email":"test@TESTEST.com"}
     * @param user a user object
     * @return ok (200) if successful badRequest if the user already exists (no overwrite)
     */
    @PostMapping("/")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user){
        if(!userRepository.findByUsername(user.getUsername()).isPresent()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            userRepository.setRole(user.getId(), RoleName.ROLE_USER.ordinal()+1);
            return ResponseEntity.ok().build();
        }
        else
            return ResponseEntity.badRequest().build();
    }

    @GetMapping("/mail/{name}")
    public String getUserEmail(@PathVariable(name = "name") String name){
        Optional<User> user = userRepository.findByUsername(name);
        if(user.isPresent()){
            return user.get().getEmail();
        }
        return("NOTFOUND");
    }
    @Secured({ROLE_ADMIN})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable(value = "id") int id){
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            userRepository.delete(user.get());
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable(name = "id") int id ,@Valid @RequestBody User user){
        Optional<User> existingUser = userRepository.findById(id);
        if(existingUser.isPresent()){
            userRepository.deleteById(id);
            User newUser = existingUser.get();
            newUser.setEmail(user.getEmail());
            newUser.setLastName(user.getLastName());
            newUser.setName(user.getName());
            newUser.setPassword(user.getPassword());
            newUser.setUsername(user.getUsername());
            userRepository.save(newUser);
        }
        return ResponseEntity.notFound().build();

    }
    /**
     * Promote user to admin (add ROLE_ADMIN authority)
     * @param username the user to be promoted's name
     * @return notFound if the user was not found in the database, badRequest if the user is already an admin,
     * ok if the operation was successful
     */
    @Secured({ROLE_ADMIN})
    @PostMapping("/makeAdmin/{user}")
    public ResponseEntity<?> makeAdmin(@PathVariable(value = "user") String username){
        Optional<User> user = userRepository.findByUsername(username);
        if(!user.isPresent())
            return ResponseEntity.notFound().build();
        if(user.get().getRoles().stream().anyMatch(role -> role.getName().equals(RoleName.ROLE_ADMIN)))
            return ResponseEntity.badRequest().build();
        userRepository.setRole(user.get().getId(),RoleName.ROLE_ADMIN.ordinal()+1);
        return ResponseEntity.ok().build();
    }


    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers("/register","/actuator/","/actuator/**","/v2/api-docs").permitAll()
                .anyRequest().authenticated();
    }
}
