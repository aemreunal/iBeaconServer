package com.aemreunal.config;

import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import com.aemreunal.helper.ImageStorage;

/*
 **************************
 * Copyright (c) 2015     *
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

@Configuration
@EnableJpaRepositories(GlobalSettings.REPOSITORY_PACKAGE_NAME)
@EnableTransactionManagement
@ComponentScan("com.aemreunal")
public class CoreConfig {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private HibernateJpaVendorAdapter vendorAdapter;

    @Autowired
    private Properties jpaProperties;

    // Required for @PropertySource and @Value value injection, as seen
    // in DatabaseSettings.java
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    // This factory scans for @Entity classes and creates the appropriate
    // mapping and wiring.
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setPackagesToScan(GlobalSettings.ENTITY_PACKAGE_NAME);
        factory.setDataSource(dataSource);
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJpaProperties(jpaProperties);
        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    // Used for resolving Multipart file uploads.
    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(GlobalSettings.MAX_UPLOAD_SIZE_BYTES);
        return resolver;
    }

    // Used for storing images in the filesystem.
    @Bean
    public ImageStorage imageStorage() {
        return new ImageStorage();
    }

    // Used for encrypting passwords.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(GlobalSettings.BCRYPT_LOG_FACTOR);
    }
}
