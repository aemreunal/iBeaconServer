package com.aemreunal.config;

/*
 * *********************** *
 * Copyright (c) 2015      *
 *                         *
 * This code belongs to:   *
 *                         *
 * @author Ahmet Emre Ünal *
 * S001974                 *
 *                         *
 * aemreunal@gmail.com     *
 * emre.unal@ozu.edu.tr    *
 *                         *
 * aemreunal.com           *
 * *********************** *
 */

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/*
 * Via: https://spring.io/guides/tutorials/rest/5/
 */

@Configuration
@EnableWebSecurity
@EnableGlobalAuthentication
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
            .dataSource(dataSource)
            .usersByUsernameQuery(GlobalSettings.USERS_FOR_AUTH_QUERY)
            .authoritiesByUsernameQuery(GlobalSettings.AUTHORITY_OF_USER_QUERY)
            .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers(GlobalSettings.USER_CREATE_MAPPING).permitAll()
                // TODO is the * necessary?
                .antMatchers(GlobalSettings.API_PATH_MAPPING + "/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .requiresChannel().antMatchers("**").requiresSecure()
                .and()
                .httpBasic()
                .and()
                .requestCache().disable()
                .rememberMe().disable()
                .headers().httpStrictTransportSecurity()
                .and()
                .csrf().disable();
    }

    private boolean allowUnsecured() {
        String envFlag = System.getenv(GlobalSettings.IBEACON_HTTP_ALLOW_KEY);
        return envFlag != null && envFlag.equalsIgnoreCase("true");
    }
}
