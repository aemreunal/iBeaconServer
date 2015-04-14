package com.aemreunal.config;

/*
 * *********************** *
 * Copyright (c) 2015      *
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
 * *********************** *
 */

public class GlobalSettings {
    /**
     * Used to set the global logging levels, like whether to log SQL queries or prints
     * inside methods.
     */
    public static final boolean DEBUGGING = true;

    /**
     * The BCrypt-hashed password field length (in User.class) is assumed to be 60 with a
     * 2-digit log factor. For example, in '$2a$10$...', the '10' is the log factor. If it
     * ever gets a 3-digit log factor (highly unlikely), the length of that field must
     * become 61.
     */
    public static final int BCRYPT_LOG_FACTOR = 10;

    /**
     * This regex matches if the given string contains a non-ASCII character. It, however,
     * does not match punctuation, so while "Hellö" matches this regex, "Hello!" won't.
     */
    public static final String NON_ASCII_REGEX = ".*[^\\p{ASCII}].*";


    /**
     * These strings provide the URIs of the controllers.
     */
    // User
    public static final String USER_PATH_MAPPING                 = "/human";
    public static final String USER_CREATE_MAPPING               = "/register";
    public static final String USER_USERNAME_MAPPING             = USER_PATH_MAPPING + "/{username}";
    public static final String USER_SPECIFIC_MAPPING             = USER_USERNAME_MAPPING;
    // Project
    public static final String PROJECT_PATH_MAPPING              = USER_SPECIFIC_MAPPING + "/projects";
    public static final String PROJECT_ID_MAPPING                = "/{projectId}";
    public static final String PROJECT_CONNECTIONS_MAPPING       = PROJECT_ID_MAPPING + "/connections";
    public static final String PROJECT_REGIONS_MAPPING           = PROJECT_ID_MAPPING + "/regions";
    public static final String PROJECT_SPECIFIC_MAPPING          = PROJECT_PATH_MAPPING + PROJECT_ID_MAPPING;
    // Region
    public static final String REGION_PATH_MAPPING               = PROJECT_SPECIFIC_MAPPING + "/regions";
    public static final String REGION_ID_MAPPING                 = "/{regionId}";
    public static final String REGION_SPECIFIC_MAPPING           = REGION_PATH_MAPPING + REGION_ID_MAPPING;
    public static final String REGION_MAP_IMAGE_MAPPING          = REGION_ID_MAPPING + "/mapimage";
    // Beacon
    public static final String BEACON_PATH_MAPPING               = REGION_SPECIFIC_MAPPING + "/beacons";
    public static final String BEACON_ID_MAPPING                 = "/{beaconId}";
    public static final String BEACON_SPECIFIC_MAPPING           = BEACON_PATH_MAPPING + BEACON_ID_MAPPING;
    public static final String BEACON_CONNECTION_MAPPING         = BEACON_ID_MAPPING + "/connection";
    // Scenario
    public static final String SCENARIO_PATH_MAPPING             = PROJECT_SPECIFIC_MAPPING + "/scenarios";
    public static final String SCENARIO_ID_MAPPING               = "/{scenarioId}";
    public static final String SCENARIO_SPECIFIC_MAPPING         = SCENARIO_PATH_MAPPING + SCENARIO_ID_MAPPING;
    public static final String SCENARIO_MEMBER_BEACONS_MAPPING   = SCENARIO_ID_MAPPING + "/beacons";
    public static final String SCENARIO_ADD_BEACON_MAPPING       = SCENARIO_ID_MAPPING + "/addbeacon";
    public static final String SCENARIO_REMOVE_BEACON_MAPPING    = SCENARIO_ID_MAPPING + "/removebeacon";
    // API
    public static final String API_PATH_MAPPING                  = "/robot";
    public static final String API_PROJECT_QUERY_PATH_MAPPING    = "/project";
    public static final String API_REGION_QUERY_PATH_MAPPING     = "/region";
    public static final String API_REGION_IMG_QUERY_PATH_MAPPING = "/regionimage";
    public static final String API_BEACON_QUERY_PATH_MAPPING     = "/beacon";


    /**
     * These strings provide the file storage locations.
     */
    public static final String USER_HOME_FOLDER_PATH               = System.getProperty("user.home");
    public static final String ROOT_STORAGE_FOLDER_DIRECTORY_NAME  = "ibeacon-server-storage";
    public static final String IMAGE_STORAGE_FOLDER_DIRECTORY_NAME = "image";
    public static final String IMAGE_STORAGE_FOLDER_PATH           = USER_HOME_FOLDER_PATH + "/" +
            ROOT_STORAGE_FOLDER_DIRECTORY_NAME + "/" +
            IMAGE_STORAGE_FOLDER_DIRECTORY_NAME + "/";
    public static final String TEXT_STORAGE_FOLDER_DIRECTORY_NAME  = "text";
    public static final String TEXT_STORAGE_FOLDER_PATH            = USER_HOME_FOLDER_PATH + "/" +
            ROOT_STORAGE_FOLDER_DIRECTORY_NAME + "/" +
            TEXT_STORAGE_FOLDER_DIRECTORY_NAME + "/";

    /**
     * These strings provide package names for annotation-based scanning.
     */
    public static final String BASE_PACKAGE_NAME       = "com.aemreunal";
    // This must be the path of the package which holds the entity classes.
    // The Entity Manager Factory scans the package designated by this string
    // to map entities.
    public static final String ENTITY_PACKAGE_NAME     = BASE_PACKAGE_NAME + ".domain";
    public static final String REPOSITORY_PACKAGE_NAME = BASE_PACKAGE_NAME + ".repository";


    //-------------------------------------------------------------------------------------------
    // Property name: "hibernate.hbm2ddl.auto"
    //
    // hibernate.hbm2ddl.auto: Automatically validates or exports schema DDL to the
    // database when the SessionFactory is created. With create-drop, the database
    // schema will be dropped when the SessionFactory is closed explicitly.
    //
    // Values: "validate" | "update" | "create" | "create-drop"
    //
    // validate:    validate the schema, makes no changes to the database.
    // update:      update the schema.
    // create:      creates the schema, destroying previous data.
    // create-drop: drop the schema at the end of the session.
    //----------------------------------------
    public static final String HBM2DDL_KEY      = "hibernate.hbm2ddl.auto";
    public static final String HBM2DDL_PROPERTY = "update";
    //-------------------------------------------------------------------------------------------


    //-------------------------------------------------------------------------------------------
    // Property name: "hibernate.show_sql"
    //----------------------------------------
    public static final String SHOW_SQL_KEY      = "hibernate.show_sql";
    public static final String SHOW_SQL_PROPERTY = String.valueOf(DEBUGGING);
    //-------------------------------------------------------------------------------------------


    //-------------------------------------------------------------------------------------------
    // Property name: "hibernate.format_sql"
    //----------------------------------------
    public static final String FORMAT_SQL_KEY      = "hibernate.format_sql";
    public static final Object FORMAT_SQL_PROPERTY = "true";
    //-------------------------------------------------------------------------------------------


    //-------------------------------------------------------------------------------------------
    // Property name: "hibernate.dialect"
    //----------------------------------------
    public static final String DB_DIALECT_KEY      = "hibernate.dialect";
    public static final String DB_DIALECT_PROPERTY = "org.hibernate.dialect.MySQL5InnoDBDialect";
    //-------------------------------------------------------------------------------------------


    //-------------------------------------------------------------------------------------------
    // Property name: "Multipart max upload size"
    //
    // This value limits the maximum Multipart upload size to the designated
    // value. Files larger than this will be rejected.
    //----------------------------------------
    public static final long MAX_UPLOAD_SIZE_BYTES = 1572864; // 1572864 Bytes (= 1.5 MB)
    //-------------------------------------------------------------------------------------------

    /**
     * Security authentication queries.
     */
    public static final String USERS_FOR_AUTH_QUERY    = "SELECT username, password, '1' FROM users WHERE username = ?";
    public static final String AUTHORITY_OF_USER_QUERY = "SELECT username, 'ADMIN' FROM users WHERE username = ?";

    /**
     * System flag for whether to allow HTTP traffic
     */
    public static final String IBEACON_HTTP_ALLOW_KEY = "IBEACON_HTTP_ALLOW";

    public static void log(String log) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println(log);
        }
    }

    public static void err(String log) {
        System.err.println(log);
    }
}
