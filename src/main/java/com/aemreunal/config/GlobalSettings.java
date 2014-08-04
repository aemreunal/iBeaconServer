package com.aemreunal.config;

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

public class GlobalSettings {
    public static final boolean DEBUGGING = true;
    public static final boolean SHOW_SQL = true;

    /*
    hibernate.hbm2ddl.auto: Automatically validates or exports schema DDL to the
    database when the SessionFactory is created. With create-drop, the database
    schema will be dropped when the SessionFactory is closed explicitly.

    Values: "validate" | "update" | "create" | "create-drop"

    validate: validate the schema, makes no changes to the database.
    update: update the schema.
    create: creates the schema, destroying previous data.
    create-drop: drop the schema at the end of the session.
     */
    //-------------------------------------------------------------------------------------------
    // Property name: "hibernate.hbm2ddl.auto"
    //----------------------------------------
    public static final String HBM2DDL_PROPERTY = "update";
    //-------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------
    // Property name: "hibernate.show_sql"
    //----------------------------------------
    public static final String SHOW_SQL_PROPERTY = "false";
    //-------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------
    // Property name: "hibernate.format_sql"
    //----------------------------------------
    public static final Object FORMAT_SQL_PROPERTY = "true";
    //-------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------
    // Property name: "hibernate.dialect"
    //----------------------------------------
    public static final String DB_DIALECT_PROPERTY = "org.hibernate.dialect.MySQL5InnoDBDialect";
    //-------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------
    // Property name: "hibernate.event.merge.entity_copy_observer"
    // Via: https://hibernate.atlassian.net/browse/HHH-9106
    //----------------------------------------
    public static final Object ENTITY_COPY_OBSERVER_PROPERTY = "allow";
    //-------------------------------------------------------------------------------------------
}
