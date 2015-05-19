#!/bin/bash

# Server address MUST BE CHANGED to the appropriate address,
# otherwise the SSL certificate will be invalid!
SERVER_ADDRESS=127.0.0.1

# iBeacon storage location, DON'T CHANGE!
STORAGE_DIR=$HOME/ibeacon_server_storage

# Environment variables for Docker
SKIP_CACHED=true
CONTAINER_NAME=ibeacon_server

# Environment variables for keystore stuff
# Set this to true if you want to delete the old keystore and
# create a new one.
CREATE_NEW_KEYSTORE=false
# If you want to change the keystore location, you must also
# update the server.xml configuration file with the correct
# location.
KEYSTORE_LOCATION=~/ibeacon_server_storage/keystore
# If you want to change the keystore password, you must also
# update the server.xml configuration file with the correct
# password.
KEYSTORE_PASSWORD=password

# Create the server storage directory (if it doesn't exist).
if [ ! -d "$STORAGE_DIR" ]; then
    mkdir $STORAGE_DIR
fi

# Delete the existing keystore if $CREATE_NEW_KEYSTORE flag
# is 'true'.
if $CREATE_NEW_KEYSTORE; then
    if [ -f "$KEYSTORE_LOCATION" ]; then
        # Delete the existing keystore
        rm $KEYSTORE_LOCATION
    fi
fi

# Create the keystore.
if [ ! -f "$KEYSTORE_LOCATION" ]; then
    # Generate the keystore
    keytool -genkey -noprompt \
     -alias tomcat \
     -keyalg RSA \
     -dname "CN=$SERVER_ADDRESS, OU=iBeacon Server, O=iBeacon Server, L=Cloud, S=Cloud, C=IB" \
     -keystore $KEYSTORE_LOCATION \
     -storepass $KEYSTORE_PASSWORD \
     -keypass $KEYSTORE_PASSWORD
fi

# Build the Docker container. This command creates a Docker
# image with the name set in $CONTAINER_NAME. The '--no-cache=true'
# argument is used to tell Docker not to use cached images
# so that Docker always pulls the latest code from GitHub.
# (This argument will be removed when Docker introduces the
# 'NOCACHE' command)
docker build --no-cache=$SKIP_CACHED -t $CONTAINER_NAME .

# Stop the pre-running container, if it is running.
if [[ -n $(docker ps | grep $CONTAINER_NAME) ]]; then
    docker stop $(docker ps | grep $CONTAINER_NAME | awk '{print $1}')
else
    echo "No running containers of name $CONTAINER_NAME found, nothing will be stopped"
fi

# Run the newly-created Docker container.
docker run --restart=always -d -p 8443:8443 -v $HOME/ibeacon-server-storage:/root/ibeacon-server-storage $CONTAINER_NAME

echo "----- End of script -----"
