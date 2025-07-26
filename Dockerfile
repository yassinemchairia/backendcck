FROM openjdk:17-alpine
WORKDIR /bcc
RUN ls -la
COPY target/cckback-0.0.1-SNAPSHOT.jar /usr/local/lib/bcc.jar
EXPOSE 8087
USER root
ENTRYPOINT ["java", "-jar", "/usr/local/lib/bcc.jar"]

