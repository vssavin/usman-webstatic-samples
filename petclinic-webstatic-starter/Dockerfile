FROM openjdk:17
WORKDIR /usr/src/petclinic-app
COPY petclinic-app /usr/src/petclinic-app

RUN java --add-opens java.base/java.lang=ALL-UNNAMED -jar spring-petclinic-3.1.0-SNAPSHOT.jar
