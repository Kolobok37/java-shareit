FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar app.jar
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081
WORKDIR /java
ENTRYPOINT ["java","-jar","/app.jar"]