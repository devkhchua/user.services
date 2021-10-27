package com.ms.resources.user.services.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.resources.user.services.util.JsonWebToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

//Custom authorization class to perform authorization through Java Spring Security
//Can be used to override existing authorization function to implement custom authorization
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private JsonWebToken jsonWebToken = new JsonWebToken();

    //Override authorization filter function from Spring Security
    //eg : if let's say there is anything that we want to verify from each and every request passing into our APIs, we can implement it here
    //For our case, we're validating the header, to see if the authorization token (access token) is valid
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //if it's login, no filter required and proceed with request/response
        log.info("[CustomAuthorizationFilter] - doFilterInternal");
        //Login and Refresh Token API doesn't need to have authorization header for it to be triggered.
        if (request.getServletPath().equals("/api/login") || request.getServletPath().equals("/api/token/refresh")) {
            filterChain.doFilter(request, response);
        } else {
            //The rest of the APIs available will be required to have Authorization Header with Bearer [Access Token]
            //Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhcHBsZSIsImV4cCI6MTYzNTkzMDc0MH0.Avr76icUPOZkoPUoYhmLMggGmx6WB0RmA6w71nGCLSg
            //Else it'll return error message
            String authHeader = request.getHeader(AUTHORIZATION);
            if (null != authHeader && authHeader.startsWith("Bearer ")) {
                try {
                    String token = authHeader.substring("Bearer ".length());
                    DecodedJWT decodedJWT = jsonWebToken.verifyToken(token);

                    String username = decodedJWT.getSubject();
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    stream(roles).forEach(role -> {
                        authorities.add(new SimpleGrantedAuthority(role));
                    });

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
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
                filterChain.doFilter(request, response);
            }
        }
    }
}
