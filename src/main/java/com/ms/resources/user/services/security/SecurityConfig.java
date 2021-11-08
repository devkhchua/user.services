package com.ms.resources.user.services.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


//Custom class to configure web security behaviour with custom behaviour.
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //Override authentication manager to use BCryptPasswordEncoder.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        log.info("[SecurityConfig] - configure!");
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    //Override spring security config to allow/disallow certain request that doesn't match your rules.
    //In our case, we're overriding default spring security login API with /auth/login/
    //Overriding the session management to be "STATELESS" will guarantee application will not create session
    //each and every request will be re-authenticated
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("[SecurityConfig] - http configure!");
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        customAuthenticationFilter.setFilterProcessesUrl("/auth/login");
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        http.authorizeRequests().antMatchers("/auth/login/**", "/auth/token/refresh/**").permitAll();
        http.authorizeRequests().antMatchers(GET, "/auth/user/**").hasAnyAuthority("BASIC");
        http.authorizeRequests().antMatchers(POST, "/auth/user/**").hasAnyAuthority("BASIC");
        http.authorizeRequests().anyRequest().authenticated();
        //http.authorizeRequests().anyRequest().permitAll();
        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        log.info("[SecurityConfig] - Http authenticationManagerBean!");
        return super.authenticationManagerBean();
    }
}
