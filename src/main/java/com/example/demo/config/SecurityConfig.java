package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
//    @Value("${AUTH_USER:admin}")
//    private String user;
//
//    @Value("${AUTH_PASSWORD:admin}")
//    private String password;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable();
//                .authorizeRequests().anyRequest();
//                .antMatchers( "/actuator/health/**").permitAll()
//                .antMatchers( "/**");
//                .authenticated().and().httpBasic();
    }

//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth)
//            throws Exception {
//        final String user = env.getProperty("AUTH_USER");
//        final String password = env.getProperty("AUTH_PASSWORD");
//
//        if (user == null || user.equals("")) {
//            throw new RuntimeException("AUTH_USER env var is empty");
//        }
//
//        if (password == null || password.equals("")) {
//            throw new RuntimeException("AUTH_PASSWORD env var is empty");
//        }
//
//        auth.inMemoryAuthentication()
//                .withUser(user)
//                .password("{noop}"+password)
//                .roles("ADMIN");
//    }
}