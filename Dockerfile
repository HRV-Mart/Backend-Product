FROM openjdk:19
ARG JAR_FILE=build/libs/Product-0.0.1-SNAPSHOT.jar
ARG MONGODB_URI=mongodb://localhost:27017
ENV MONGODB_URI=$MONGODB_URI
ENV PORT 8081
EXPOSE 8081 27017
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
