FROM openjdk:14-jdk-alpine AS base
WORKDIR /usr/src/app
RUN apk --no-cache add curl
EXPOSE 8080

FROM maven:3-jdk-14 AS dependency
COPY pom.xml /build/
WORKDIR /build/
RUN mvn compile dependency:copy-dependencies -P PRODUCTION

FROM dependency as build
COPY src /build/src/
WORKDIR /build/
RUN mvn package -P PRODUCTION

FROM base AS final
WORKDIR /usr/src/app
ARG ENVIRONMENT_NAME=production
COPY --from=build /build/target/*.jar app.jar
ENTRYPOINT ["java","-jar","-XX:+UnlockExperimentalVMOptions","-XX:+UseZGC","-Xmx256m","-Dspring.profiles.active=${ENVIRONMENT_NAME}","app.jar"]
