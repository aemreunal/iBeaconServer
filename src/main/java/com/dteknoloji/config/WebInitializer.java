package com.dteknoloji.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class WebInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();

        webApplicationContext.register(CoreConfig.class, MVCConfig.class);
        servletContext.addListener(new ContextLoaderListener(webApplicationContext));

        AnnotationConfigWebApplicationContext dispatchCtx = new AnnotationConfigWebApplicationContext();

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(dispatchCtx));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}
