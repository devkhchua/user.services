package com.ms.resources.user.services.impl;

import com.ms.resources.user.services.entity.User;
import com.ms.resources.user.services.repository.UserRepository;
import com.ms.resources.user.services.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j

//Implementor class to implement functions to store into database.
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //Override function in UserDetailsService class (from spring security)
    //To retrieve user's to be authenticated from user database table in MySQL instead of default spring security admin/users
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            log.error("[UserServiceImpl] - User not found in database");
            throw new UsernameNotFoundException("User not found in database");
        }

        log.info("[UserServiceImpl] - User found in database : {} ", user.getUsername());

        //No roles required, hence creating a basic roles for all users.
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("BASIC"));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public List<User> findAllUser(){
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    @Override
    public User findUserByUsername(String username) {
        log.info("[UserServiceImpl] findUserByUsername - : " + username);
        return userRepository.findByUsername(username);
    }
}
