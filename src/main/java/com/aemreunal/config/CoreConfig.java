package com.aemreunal.config;

import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import com.aemreunal.helper.ImageStorage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

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

@Configuration
@EnableJpaRepositories(basePackages = { "com.aemreunal.repository" })
@EnableTransactionManagement
@ComponentScan(basePackages = { "com.aemreunal" })
public class CoreConfig {
    // Limit maximum upload size to 1572864 Bytes (= 1.5 MB)
    public static final long MAX_UPLOAD_SIZE_BYTES = 1572864;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private HibernateJpaVendorAdapter vendorAdapter;

    // Required for @PropertySource and @Value value injection, as seen
    // in DatabaseSettings.java
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.aemreunal.domain");
        factory.setDataSource(dataSource);
        factory.setJpaProperties(jpaProperties());
        return factory;
    }


    @Bean
    public MappingJackson2HttpMessageConverter jacksonMessageConverter() {
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();
        //Registering Hibernate4Module to support lazy objects
        mapper.registerModule(new Hibernate4Module());
        messageConverter.setObjectMapper(mapper);
        return messageConverter;
    }

    @Bean
    public ImageStorage imageStorage() {
        return new ImageStorage();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(MAX_UPLOAD_SIZE_BYTES);
        return resolver;
    }

    private Properties jpaProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", GlobalSettings.DB_DIALECT_PROPERTY);
        properties.put("hibernate.show_sql", GlobalSettings.SHOW_SQL_PROPERTY);
        properties.put("hibernate.format_sql", GlobalSettings.FORMAT_SQL_PROPERTY);
        properties.put("hibernate.hbm2ddl.auto", GlobalSettings.HBM2DDL_PROPERTY);
        return properties;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(GlobalSettings.BCRYPT_LOG_FACTOR);
    }

    public static void initLazily(Object proxy) {
        if (!Hibernate.isInitialized(proxy)) {
            Hibernate.initialize(proxy);
        }
    }
}
