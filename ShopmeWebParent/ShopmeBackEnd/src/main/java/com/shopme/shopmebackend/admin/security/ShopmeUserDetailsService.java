package com.shopme.shopmebackend.admin.security;

import com.shopme.shopmebackend.admin.entity.User;
import com.shopme.shopmebackend.admin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class ShopmeUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if(user != null){
            return new ShopmeUserDetails(user);
        }
        throw new UsernameNotFoundException("Could not find user with email:"+username);
    }
}
