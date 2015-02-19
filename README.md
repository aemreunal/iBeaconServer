iBeaconServer
=============

Server application for iBeacon management.

## Setting up

You need to have MySQL running, with an empty database. The server will set the database tables up itself. To point the server to the correct database, you have to fill the correct values (like the database credentials, IP address, etc.) in the property file called ```db.properties```, located at the root of the repository.

A dummy ```db.properties``` file is provided in the [Docker](https://github.com/aemreunal/iBeaconServer/tree/master/Docker) folder.

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
        |-- db.properties
        |-- deploy.sh
        |-- Dockerfile
        |-- server.xml
        |-- setenv.sh
        |-- web.xml
    ```

2. Fill the database server information in the ```db.properties``` file.

3. Run the `deploy.sh` script, which will create the necessary folders and the keystore used by the Tomcat instance inside the container, build the Docker container, and run it. The default settings are not really secure (like the keystore settings) but it will provide a good starting point. These settings should be changed by the user for proper security.
