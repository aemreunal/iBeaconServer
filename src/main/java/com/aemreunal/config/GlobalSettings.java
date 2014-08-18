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

    /*
     * The BCrypt-hashed password field length (in User.class) is assumed to
     * be 60 with a 2-digit log factor. For example, in '$2a$10$...', the '10'
     * is the log factor. If it ever gets a 3-digit log factor (highly
     * unlikely), the length of that field must become 61.
     */
    public static final int BCRYPT_LOG_FACTOR = 10;

    /*
     * This context path is defined inside the gradle build script,
     * named 'build.gradle'. This value only reflects that one and
     * if that context path is changed, this must be changed as
     * well, since tests rely on the correctness of this value.
     */
    public static final String BASE_CONTEXT_PATH = "iBeacon";

//    public static final String USER_PATH_MAPPING     = "/user";
    public static final String USER_PATH_MAPPING     = "";
    public static final String USER_USERNAME_MAPPING = "/{username}";
    public static final String USER_SPECIFIC_MAPPING = USER_PATH_MAPPING + USER_USERNAME_MAPPING;

    public static final String PROJECT_PATH_MAPPING     = USER_SPECIFIC_MAPPING + "/project";
    public static final String PROJECT_ID_MAPPING       = "/{projectId}";
    public static final String PROJECT_SPECIFIC_MAPPING = PROJECT_PATH_MAPPING + PROJECT_ID_MAPPING;

    public static final String BEACON_PATH_MAPPING     = PROJECT_SPECIFIC_MAPPING + "/beacon";
    public static final String BEACON_ID_MAPPING       = "/{beaconId}";
    public static final String BEACON_SPECIFIC_MAPPING = BEACON_PATH_MAPPING + BEACON_ID_MAPPING;

    public static final String BEACONGROUP_PATH_MAPPING          = PROJECT_SPECIFIC_MAPPING + "/beacongroup";
    public static final String BEACONGROUP_ID_MAPPING            = "/{beaconGroupId}";
    public static final String BEACONGROUP_SPECIFIC_MAPPING      = BEACONGROUP_PATH_MAPPING + BEACONGROUP_ID_MAPPING;
    public static final String BEACONGROUP_MEMBERS_MAPPING       = "/beacons";
    public static final String BEACONGROUP_ADD_MEMBER_MAPPING    = BEACONGROUP_ID_MAPPING + "/addbeacontogroup";
    public static final String BEACONGROUP_REMOVE_MEMBER_MAPPING = BEACONGROUP_ID_MAPPING + "/removebeaconfromgroup";


    //-------------------------------------------------------------------------------------------
    // Property name: "hibernate.hbm2ddl.auto"
    //
    // hibernate.hbm2ddl.auto: Automatically validates or exports schema DDL to the
    // database when the SessionFactory is created. With create-drop, the database
    // schema will be dropped when the SessionFactory is closed explicitly.
    //
    // Values: "validate" | "update" | "create" | "create-drop"
    //
    // validate: validate the schema, makes no changes to the database.
    // update: update the schema.
    // create: creates the schema, destroying previous data.
    // create-drop: drop the schema at the end of the session.
    //----------------------------------------
    public static final String HBM2DDL_PROPERTY = "update";
    //-------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------
    // Property name: "hibernate.show_sql"
    //----------------------------------------
    public static final String  SHOW_SQL_PROPERTY = "true";
    public static final boolean SHOW_SQL          = true;
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
}
