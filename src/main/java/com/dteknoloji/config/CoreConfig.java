package com.dteknoloji.config;

import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
@EnableJpaRepositories(basePackages = { "com.dteknoloji.repository" })
@EnableTransactionManagement
@ComponentScan(basePackages = { "com.dteknoloji" })
public class CoreConfig {
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(GlobalSettings.DB_TYPE);
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(GlobalSettings.SHOW_SQL);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.dteknoloji.domain");
        factory.setDataSource(dataSource());
        factory.setJpaProperties(jpaProperties());
        return factory;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(GlobalSettings.DB_DRIVER_CLASS_NAME);
        dataSource.setUrl(GlobalSettings.DB_URL);
        dataSource.setUsername(GlobalSettings.DB_USERNAME);
        dataSource.setPassword(GlobalSettings.DB_PASSWORD);

        return dataSource;
    }

    private Properties jpaProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", GlobalSettings.DB_DIALECT_PROPERTY);
        properties.put("hibernate.show_sql", GlobalSettings.SHOW_SQL_PROPERTY);
        properties.put("hibernate.hbm2ddl.auto", GlobalSettings.HBM2DDL_PROPERTY);
//        properties.put("hibernate.event.merge.entity_copy_observer", GlobalSettings.ENTITY_COPY_OBSERVER_PROPERTY);
        return properties;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }
}
