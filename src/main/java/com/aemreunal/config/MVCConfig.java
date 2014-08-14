package com.aemreunal.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/*
 **************************
 * Copyright (c) 2014     *
 *                        *
 * This code belongs to:  *
 *                        *
 * Ahmet Emre Ünal        *
 * S001974                *
 *                        *
 * aemreunal@gmail.com    *
 * emre.unal@ozu.edu.tr   *
 *                        *
 * aemreunal.com          *
 **************************
 */

/*
 * http://www.petrikainulainen.net/programming/spring-framework/unit-testing-of-spring-mvc-controllers-configuration/
 */

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.aemreunal" })
public class MVCConfig extends WebMvcConfigurerAdapter {
    // TODO find out what this class is used for

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        // TODO Add security interceptor for HTTP to HTTPS redirect
        // http://docs.spring.io/spring/docs/4.0.0.RELEASE/spring-framework-reference/htmlsingle/#mvc-container-config

//        registry.addInterceptor(new FilterSecurityInterceptor()).addPathPatterns("/secure/*");
//        registry.addInterceptor().addPathPatterns("/secure/*");
    }
}
