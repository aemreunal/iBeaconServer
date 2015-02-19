package com.aemreunal.config;


import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

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

@Configuration
@PropertySource("file:db.properties")
public class DatabaseSettings {
    private static final String DB_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    @Value("${db.ip}")
    private String dbIp;

    @Value("${db.port}")
    private String dbPort;

    @Value("${db.name}")
    private String dbName;

    @Bean
    public HibernateJpaVendorAdapter vendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(DatabaseSettings.DB_TYPE);
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(GlobalSettings.SHOW_SQL);
        return vendorAdapter;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DB_DRIVER_CLASS_NAME);
        dataSource.setUrl(getDbUrl());
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }

    private String getDbUrl() {
        return "jdbc:mysql://" + dbIp + ":" + dbPort + "/" + dbName + "?useUnicode=true&characterEncoding=UTF-8";
    }

    public static final Database DB_TYPE = Database.MYSQL;
}
