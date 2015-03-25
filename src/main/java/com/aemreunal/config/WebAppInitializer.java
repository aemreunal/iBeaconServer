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

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        initDispatchContext(servletContext);
        initWebAppContext(servletContext);
    }

    private void initDispatchContext(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext dispatchContext = new AnnotationConfigWebApplicationContext();
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(dispatchContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }

    private void initWebAppContext(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext webAppContext = new AnnotationConfigWebApplicationContext();
        webAppContext.register(CoreConfig.class, MVCConfig.class, SecurityConfig.class);
        servletContext.addListener(new ContextLoaderListener(webAppContext));

        addSecurityFilterToChain(servletContext, webAppContext);
        addUserUrlFilterToChain(servletContext);
    }

    private void addSecurityFilterToChain(ServletContext servletContext, WebApplicationContext webAppContext) {
        FilterRegistration.Dynamic springSecurity = servletContext.addFilter("springSecurityFilterChain", new DelegatingFilterProxy("springSecurityFilterChain", webAppContext));
        springSecurity.addMappingForUrlPatterns(null, false, "/*");
    }

    private void addUserUrlFilterToChain(ServletContext servletContext) {
        FilterRegistration.Dynamic userURLFilter = servletContext.addFilter("userUrlFilterChain", UserUrlFilter.class);
        userURLFilter.addMappingForUrlPatterns(null, false, "/human/*");
    }
}
