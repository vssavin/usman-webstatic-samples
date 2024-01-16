# FROM maven:3.8.2-jdk-8 # for Java 8
FROM maven:3.8.5-openjdk-17

WORKDIR /petclinic-app

COPY . .
RUN mvn clean install -Dmaven.test.skip=true

#CMD mvn spring-boot:run -Dspring-boot.run.profiles=mysql -Dspring-boot.run.jvmArguments="--add-opens java.base/java.lang=ALL-UNNAMED"
RUN mvn spring-boot:run -Dspring-boot.run.profiles=mysql -Dspring-boot.run.jvmArguments="--add-opens java.base/java.lang=ALL-UNNAMED"
