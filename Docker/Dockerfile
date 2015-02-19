# From official Tomcat 8 - JDK 8 Dockerfile:
FROM java:8-jdk

MAINTAINER docker@aemreunal.com

ENV CATALINA_HOME /usr/local/tomcat
ENV PATH $CATALINA_HOME/bin:$PATH
RUN mkdir -p "$CATALINA_HOME"
WORKDIR $CATALINA_HOME

# see https://www.apache.org/dist/tomcat/tomcat-8/KEYS
RUN gpg --keyserver pool.sks-keyservers.net --recv-keys \
    05AB33110949707C93A279E3D3EFE6B686867BA6 \
    07E48665A34DCAFAE522E5E6266191C37C037D42 \
    47309207D818FFD8DCD3F83F1931D684307A10A5 \
    541FBE7D8F78B25E055DDEE13C370389288584E7 \
    61B832AC2F1C5A90F0F9B00A1C506407564C17A3 \
    79F7026C690BAA50B92CD8B66A3AD3F4F22C4FED \
    9BA44C2621385CB966EBA586F72C284D731FABEE \
    A27677289986DB50844682F8ACB77FC2E86E29AC \
    A9C5DF4D22E99998D9875A5110C01C5A2F6059E7 \
    DCFD35E0BF8CA7344752DE8B6FB21E8933C60243 \
    F3A04C595DB5B6A5F1ECA43E3B7BBB100D811BBE \
    F7DA48BB64BCB84ECBA7EE6935CD23C10D498E23

ENV TOMCAT_MAJOR 8
ENV TOMCAT_VERSION 8.0.18
ENV TOMCAT_TGZ_URL https://www.apache.org/dist/tomcat/tomcat-$TOMCAT_MAJOR/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz

RUN curl -SL "$TOMCAT_TGZ_URL" -o tomcat.tar.gz \
    && curl -SL "$TOMCAT_TGZ_URL.asc" -o tomcat.tar.gz.asc \
    && gpg --verify tomcat.tar.gz.asc \
    && tar -xvf tomcat.tar.gz --strip-components=1 \
    && rm bin/*.bat \
    && rm tomcat.tar.gz*

# iBeacon server addition:

# Copy custom server.xml and web.xml to Tomcat conf
RUN rm $CATALINA_HOME/conf/server.xml
COPY server.xml $CATALINA_HOME/conf/server.xml
RUN rm $CATALINA_HOME/conf/web.xml
COPY web.xml $CATALINA_HOME/conf/web.xml

# Install debconf, git, and locales
RUN apt-get update && apt-get install -y \
    debconf \
    git \
    locales

# Set the locale
ENV DEBIAN_FRONTEND noninteractive
RUN dpkg-reconfigure locales \
    && locale-gen C.UTF-8 \
    && /usr/sbin/update-locale LANG=C.UTF-8
ENV LC_ALL C.UTF-8

# Stop caching from this point forward
# This is done to ensure that the latest code is pulled
# from GitHub.
# NOTE: Disabled until integrated to Docker!
#NOCACHE

# Clone iBeaconServer repo
RUN cd ~/ \
    && git clone https://github.com/aemreunal/iBeaconServer.git

# Add required database settings property file to project
COPY db.properties /root/iBeaconServer/db.properties
# Create WAR file
RUN cd ~/iBeaconServer/ \
    && ./gradlew war

# Delete other webapps in Tomcat
RUN rm -rf $CATALINA_HOME/webapps/*
# Add iBeacon server webapp as root webapp
RUN cd ~/iBeaconServer/ \
    && cp build/libs/iBeacon.war $CATALINA_HOME/webapps/ROOT.war
# Add non-secure random gen. env. option setter
COPY setenv.sh $CATALINA_HOME/bin/setenv.sh

EXPOSE 8080

CMD ["catalina.sh", "run"]
