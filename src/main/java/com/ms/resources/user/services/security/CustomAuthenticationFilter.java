package com.ms.resources.user.services.security;

import com.ms.resources.user.services.util.JsonWebToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

//Custom authentication class to perform authentication through Java Spring Security
//Can be used to override existing authentication function to implement custom authentication
@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private JsonWebToken jsonWebToken = new JsonWebToken();

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    };

    //Override authentication function from Spring Security
    //eg : if let's say there is a third party handling accounts/authentication, and implement it here.
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("[CustomAuthenticationFilter] - attemptAuthentication");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        log.info("User authentication : " + username + " " + password);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    //Override after successfully authenticated function from Spring Security
    //eg : if successfully authentication we can implement custom behaviour
    //For our case, we implement generating access/refresh (JWT token) for authenticated users
    //so that we can use it as authorization for API calls.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("[CustomAuthenticationFilter] - successfulAuthentication");
        User user = (User) authResult.getPrincipal();
        List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        String access_token = jsonWebToken.generateAccessToken(user.getUsername(), request, roles);
        String refresh_token = jsonWebToken.generateRefreshToken(user.getUsername());
        response.setHeader("access_token", access_token);
        response.setHeader("refresh_token", refresh_token);
    }
}
