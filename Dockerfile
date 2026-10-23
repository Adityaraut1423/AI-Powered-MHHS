FROM eclipse-temurin:17-jdk-alpine 
VOLUME /tmp 
COPY target/mindspace-0.0.1-SNAPSHOT.jar app.jar 
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/app.jar"] 
