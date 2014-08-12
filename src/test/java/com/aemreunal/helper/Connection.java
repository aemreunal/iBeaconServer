package com.aemreunal.helper;

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

import java.sql.SQLException;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import com.aemreunal.config.DatabaseSettings;

public class Connection {
    private static DriverManagerDataSource dataSource = null;
    private static IDatabaseConnection connection = null;

    public static IDatabaseConnection getConnection() {
        if (connection == null) {
            createConnection();
        }
        return connection;
    }

    private static DriverManagerDataSource getDataSource() {
        if (dataSource == null) {
            createDataSource();
        }
        return dataSource;
    }

    private static void createDataSource() {
        dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DatabaseSettings.DB_DRIVER_CLASS_NAME);
        dataSource.setUrl(DatabaseSettings.DB_URL);
        dataSource.setUsername(DatabaseSettings.DB_USERNAME);
        dataSource.setPassword(DatabaseSettings.DB_PASSWORD);
    }

    private static void createConnection() {
        try {
            java.sql.Connection jdbcConnection = getDataSource().getConnection();
            connection = new DatabaseConnection(jdbcConnection);
        } catch (SQLException e) {
            System.err.println("Unable to get connection!");
            e.printStackTrace();
        } catch (DatabaseUnitException e) {
            System.err.println("Unable to create database connection!");
            e.printStackTrace();
        }
    }
}
