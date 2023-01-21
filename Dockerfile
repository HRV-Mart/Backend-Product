FROM openjdk:19
ARG JAR_FILE=build/libs/Product-0.0.1-SNAPSHOT.jar
ARG MONGODB_URI=mongodb://localhost:27017
ARG PORT=8080
ENV MONGODB_URI=$MONGODB_URI
ENV PORT=$PORT
EXPOSE PORT 27017
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]