package com.ms.resources.user.services.controller;

import com.ms.resources.user.services.entity.User;
import com.ms.resources.user.services.impl.UserServiceImpl;
import com.ms.resources.user.services.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.websocket.server.PathParam;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/user/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@RequestBody User user){
        log.info("Creating user {} to the database", user.getName());
        return new ResponseEntity<User>(userService.createUser(user), HttpStatus.CREATED);
    }

    @GetMapping("/user/retrieve/all")
    public ResponseEntity<List<User>> retrieveUser(){
        log.info("Retrieving all user from the database ");
        return ResponseEntity.ok().body((userService.findAllUser()));
    }

    @GetMapping("/user/retrieve/{username}")
    public ResponseEntity<User> retrieveUser(@PathVariable("username") String username){
        log.info("Retrieving user from the database " + username );
        return ResponseEntity.ok().body((userService.findUserByUsername(username)));
    }

    @GetMapping("/user/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok().body("Return message Test !");
    }
}
