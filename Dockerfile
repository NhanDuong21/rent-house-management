FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
COPY renthouse-config ./renthouse-config
COPY nb-configuration.xml ./
COPY README.md ./
COPY RentHouse.sql ./

RUN mvn -B clean package -DskipTests

FROM tomcat:10.1-jdk21-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]