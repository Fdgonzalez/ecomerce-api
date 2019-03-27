package com.cloud.ecomerce.restauth;

import com.cloud.ecomerce.restauth.model.Role;
import com.cloud.ecomerce.restauth.model.UserModel;
import com.cloud.ecomerce.restauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<UserModel> possibleUser = userRepository.findByUsername(s);
        if(!possibleUser.isPresent())
            throw new UsernameNotFoundException(s);
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>(4);
        for (Role r: possibleUser.get().getRoles()) {
            authorities.add(new SimpleGrantedAuthority(r.getName().name()));
        }
        return User.builder()
                .username(possibleUser.get().getUsername())
                .password(possibleUser.get().getPassword())
                .authorities(authorities)
                .build();
    }
}
