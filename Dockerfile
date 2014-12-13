# To build: docker build -t aemreunal/ibeacon_server .
# To run: docker run --restart=always -d -p 8080:8080 aemreunal/ibeacon_server

FROM codenvy/jdk7

RUN mkdir /home/user/tomcat7 && \
    wget -qO- "http://archive.apache.org/dist/tomcat/tomcat-7/v7.0.53/bin/apache-tomcat-7.0.53.tar.gz" | tar -zx --strip-components=1 -C /home/user/tomcat7 && \
    rm -rf /home/user/tomcat7/webapps/*

EXPOSE 8080

ENV CODENVY_APP_PORT_8080_HTTP 8080

ADD iBeacon.war /home/user/tomcat7/webapps/ROOT.war

WORKDIR /home/user/tomcat7/bin

CMD ./catalina.sh run
