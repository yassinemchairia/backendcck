FROM openjdk:17-alpine
WORKDIR /bcc
RUN ls -la
COPY target/carecareforEldres-0.1.jar /usr/local/lib/bcc.jar
EXPOSE 8080
USER root
ENTRYPOINT ["java", "-jar", "/usr/local/lib/5SE1G5DEVOPSBACKEND.jar"]
