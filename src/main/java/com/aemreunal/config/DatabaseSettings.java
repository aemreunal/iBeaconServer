package com.aemreunal.config;

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

import java.beans.PropertyVetoException;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mysql.jdbc.Driver;

@Configuration
@PropertySource("file:db.properties")
public class DatabaseSettings {
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
        vendorAdapter.setDatabase(Database.MYSQL);
        vendorAdapter.setGenerateDdl(true);
        return vendorAdapter;
    }


    // TODO better configure data source:
    // http://www.mchange.com/projects/c3p0/#configuration
    // http://stackoverflow.com/questions/12446266/c3p0-configurations-where-and-how
    // http://www.javacodegeeks.com/2014/05/adding-c3po-connection-pooling-in-spring-jdbc.html
    // http://stackoverflow.com/questions/23480106/my-springhibernate-app-does-not-closes-jdbc-connections
    @Bean
    public DataSource dataSource() {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass(Driver.class.getName());
        } catch (PropertyVetoException e) {
            System.err.println("Unable to set data source driver class!\nWill exit now.");
            System.exit(-1);
        }
        dataSource.setJdbcUrl(getJdbcUrl());
        dataSource.setUser(dbUsername);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }

    @Bean
    public Properties jpaProperties() {
        Properties properties = new Properties();
        properties.put(GlobalSettings.DB_DIALECT_KEY, GlobalSettings.DB_DIALECT_PROPERTY);
        properties.put(GlobalSettings.SHOW_SQL_KEY, GlobalSettings.SHOW_SQL_PROPERTY);
        properties.put(GlobalSettings.FORMAT_SQL_KEY, GlobalSettings.FORMAT_SQL_PROPERTY);
        properties.put(GlobalSettings.HBM2DDL_KEY, GlobalSettings.HBM2DDL_PROPERTY);
        return properties;
    }

    // We need to call this dynamically and can't make it a field,
    // as the other fields this depends on are assigned dynamically
    // during runtime via '@Value'
    private String getJdbcUrl() {
        return "jdbc:mysql://" + dbIp + ":" + dbPort + "/" + dbName + "?useUnicode=true&characterEncoding=UTF-8";
    }

}
