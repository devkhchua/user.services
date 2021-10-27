package com.ms.resources.user.services.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.resources.user.services.entity.User;
import com.ms.resources.user.services.services.UserService;
import com.ms.resources.user.services.util.JsonWebToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    private JsonWebToken jsonWebToken = new JsonWebToken();

    //Create users into database
    @PostMapping(value = "/user/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@RequestBody User user) {
        log.info("Creating user {} to the database", user.getName());
        return new ResponseEntity<User>(userService.createUser(user), HttpStatus.CREATED);

    }

    //Retrieve all available users
    @GetMapping("/user/retrieve/all")
    public ResponseEntity<List<User>> retrieveUser() {
        try {
            log.info("Retrieving all user from the database ");
            return ResponseEntity.ok().body((userService.findAllUser()));
        } catch (Exception e) {

        }

        return null;
    }

    //Retrieve specific user
    @GetMapping("/user/retrieve/{username}")
    public ResponseEntity<User> retrieveUser(@PathVariable("username") String username) {
        log.info("Retrieving user from the database " + username);
        return ResponseEntity.ok().body((userService.findUserByUsername(username)));
    }

    //Test function
    @GetMapping("/user/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok().body("Return message Test !");
    }

    //Refresh token when access token has expired
    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader(AUTHORIZATION);
        if (null != authHeader && authHeader.startsWith("Bearer ")) {
            try {
                String refreshToken = authHeader.substring("Bearer ".length());
                DecodedJWT decodedJWT = jsonWebToken.verifyToken(refreshToken);

                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("BASIC"));
                List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

                String username = decodedJWT.getSubject();
                User user = userService.findUserByUsername(username);
                String access_token = jsonWebToken.generateAccessToken(user.getUsername(), request, roles);

                response.setHeader("access_token", access_token);
                response.setHeader("refresh_token", refreshToken);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            } catch (Exception e) {
                log.error("Error message : " + e.getMessage());
                response.setHeader("error ", e.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh Token is Missing !");
        }
    }
}
