FROM openjdk:17-jdk-oraclelinux7
WORKDIR /usr/src/petclinic-app
COPY petclinic-app /usr/src/petclinic-app

RUN yum update -y && \
    yum upgrade -y && \
    yum install udev -y && \
    java --add-opens java.base/java.lang=ALL-UNNAMED -jar spring-petclinic-3.1.0-SNAPSHOT.jar
