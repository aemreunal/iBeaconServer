iBeaconServer
=============

Server application for iBeacon management.

## Setting up

You need to have MySQL running, with an empty database. The server will set the database up itself. To point the server to the correct direction, you have to create a Java source file at: `src/main/java/com/aemreunal/config/DatabaseSettings.java`, with the following structure:

```java
package com.aemreunal.config;

import org.springframework.orm.jpa.vendor.Database;

public class DatabaseSettings {
    // The address of the database you want to use
    // It can be a local or remote database
    public static final String DB_IP   = "192.168.1.1";
    public static final String DB_PORT = "9999";

    // The name of the database you want to use
    public static final String DB_NAME = "name_of_database_for_ibeacon_server";

    // The username and password for the database you want to use
    public static final String DB_USERNAME = "ibeacon_master";
    public static final String DB_PASSWORD = "very_strong_password";

    // Don't change these.
    public static final String   DB_URL = "jdbc:mysql://" + DB_IP + ":" + DB_PORT + "/" + DB_NAME + "?useUnicode=true&characterEncoding=UTF-8";
    public static final Database DB_TYPE = Database.MYSQL;
    public static final String   DB_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
}
```

A blank DatabaseSettings.java file is provided in the [Docker](https://github.com/aemreunal/iBeaconServer/tree/master/Docker) folder.

## Test Run

To test the server by running it, execute `./gradlew tomcatRunWar` for *nix, or `gradlew.bat tomcatRunWar` for Windows.

## Production

By executing `./gradlew war` (or `gradlew.bat war` for Windows), you can create a WAR file of the server, which you can deploy to a Tomcat instance. The WAR file will be inside `build/libs/`.

## iBeacon Docker container

To use iBeacon server inside a Docker container:

1. Take the files in the [Docker](https://github.com/aemreunal/iBeaconServer/tree/master/Docker) folder, put them inside a seperate folder. For example:

    ```
    ibeaconserver
       \
        |-- DatabaseSettings.java
        |-- deploy.sh
        |-- Dockerfile
        |-- server.xml
        |-- setenv.sh
        |-- web.xml
    ```

2. Fill the database server information in the DatabaseSettings.java file.

3. Run the `deploy.sh` script, which will create the necessary folders and the keystore used by the Tomcat instance inside the container, build the Docker container, and run it. The default settings are not really secure (like the keystore settings) but it will provide a good starting point. These settings should be changed by the user for proper security.
