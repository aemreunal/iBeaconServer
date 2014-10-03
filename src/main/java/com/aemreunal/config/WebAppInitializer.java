package com.aemreunal.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

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

public class WebAppInitializer implements WebApplicationInitializer {
    // TODO find out what this class is used for

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext webAppContext = new AnnotationConfigWebApplicationContext();
        webAppContext.register(CoreConfig.class, MVCConfig.class, SecurityConfig.class);
        servletContext.addListener(new ContextLoaderListener(webAppContext));

        AnnotationConfigWebApplicationContext dispatchCtx = new AnnotationConfigWebApplicationContext();
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(dispatchCtx));

        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }


}
