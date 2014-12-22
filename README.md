iBeaconServer
=============

Server application for iBeacon management.

## Setting up

You need to have MySQL running, with an empty database. The server will set the database up itself. To point the server to the correct direction, you have to create a Java source file at: `src/main/java/com/aemreunal/config/DatabaseSettings.java`, with the following structure:

```java
package com.aemreunal.config;

import org.springframework.orm.jpa.vendor.Database;

public class DatabaseSettings {
    // The name of the database you want to use
    public static final String DB_NAME = "name_of_database_for_ibeacon_server";

    // The address of the database you want to use
    // It can be a local or remote database
    public static final String DB_IP   = "192.168.1.1";
    public static final String DB_PORT = "9999";

    // No need to change this.
    public static final String DB_URL = "jdbc:mysql://" + DB_IP + ":" + DB_PORT + "/" + DB_NAME + "?useUnicode=true&characterEncoding=UTF-8";

    // No need to change this.
    public static final Database DB_TYPE              = Database.MYSQL;
    public static final String   DB_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

    // The username and password for the database you want to use
    public static final String DB_USERNAME = "ibeacon_master";
    public static final String DB_PASSWORD = "very_strong_password";
}
```

## Test Run

To test the server by running it, you can execute `./gradlew tomcatRunWar` for *nix, or `gradlew.bat tomcatRunWar` for Windows.

## Production

By executing `./gradlew war` (or `gradlew.bat war` for Windows), you can create a WAR file of the server, which you can deploy to a Tomcat instance. The WAR file will be inside `build/libs/`.

## iBeacon Docker container

To use iBeacon server inside a Docker container:

1. Follow the setup directions below to create the necessary Java source file.

2. Create the iBeacon server WAR file.

3. Take the files in the [Docker](https://github.com/aemreunal/iBeaconServer/tree/master/Docker) folder and the WAR file, put them inside a seperate folder. For example:

    ```
    ibeaconserver
       \
        |-- Dockerfile
        |-- iBeacon.war
        |-- setenv.sh
    ```

4. Run `docker build -t ibeaconserver .` to create a Docker image with the name `ibeaconserver`.

5. Run `docker run --restart=always -d -p 8080:8080 ibeaconserver` to run the Docker container.
