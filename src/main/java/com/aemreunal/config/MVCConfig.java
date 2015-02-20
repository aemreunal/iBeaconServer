package com.aemreunal.config;

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

import java.util.List;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

/*
 * http://www.petrikainulainen.net/programming/spring-framework/unit-testing-of-spring-mvc-controllers-configuration/
 */

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.aemreunal" })
public class MVCConfig extends WebMvcConfigurerAdapter {
    public MappingJackson2HttpMessageConverter jacksonMessageConverter() {
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();
        //Registering Hibernate4Module to support lazy objects
        mapper.registerModule(new Hibernate4Module());
        messageConverter.setObjectMapper(mapper);
        return messageConverter;
    }

    public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
        return new ByteArrayHttpMessageConverter();
    }

//    public FormHttpMessageConverter formHttpMessageConverter() {
//        return new FormHttpMessageConverter();
//    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //Here we add our custom-configured HttpMessageConverters
        converters.add(jacksonMessageConverter());
        converters.add(byteArrayHttpMessageConverter());
        super.configureMessageConverters(converters);
    }
}
