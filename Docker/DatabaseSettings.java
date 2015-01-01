package com.aemreunal.config;

import org.springframework.orm.jpa.vendor.Database;

public class DatabaseSettings {
    // The address of the database you want to use
    // It can be a local or remote database
    public static final String DB_IP   = "";
    public static final String DB_PORT = "";

    // The name of the database you want to use
    public static final String DB_NAME = "";

    // The username and password for the database you want to use
    public static final String DB_USERNAME = "";
    public static final String DB_PASSWORD = "";

    // Don't change these.
    public static final String   DB_URL = "jdbc:mysql://" + DB_IP + ":" + DB_PORT + "/" + DB_NAME + "?useUnicode=true&characterEncoding=UTF-8";
    public static final Database DB_TYPE = Database.MYSQL;
    public static final String   DB_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
}
