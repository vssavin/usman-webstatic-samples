FROM openjdk:17-ea-17-jdk-oraclelinux7
WORKDIR /usr/src/petclinic-app
COPY petclinic-app /usr/src/petclinic-app

RUN yum update -y && \
    yum upgrade -y && \
    yum install udev -y

EXPOSE 8080

CMD JAR_FILENAME="$(find *.jar)" && java --add-opens java.base/java.lang=ALL-UNNAMED -Dspring.profiles.active=${ACTIVE_PROFILE} -jar ${JAR_FILENAME}
