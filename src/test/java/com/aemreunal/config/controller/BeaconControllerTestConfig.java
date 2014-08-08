package com.aemreunal.config.controller;

/*
 ***************************
 * Copyright (c) 2014      *
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
 ***************************
 */


import java.nio.charset.Charset;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.aemreunal.controller.BeaconController;
import com.aemreunal.service.BeaconService;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.aemreunal" })
//@ContextConfiguration(classes = { CoreTestConfig.class })
public class BeaconControllerTestConfig {
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
                                                                        MediaType.APPLICATION_JSON.getSubtype(),
                                                                        Charset.forName("utf8"));

    @Bean
    public BeaconController beaconController() {
        return new BeaconController();
    }

    @Bean
    public BeaconService beaconService() {
        return new BeaconService();
    }
}
