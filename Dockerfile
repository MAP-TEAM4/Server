FROM openjdk:11

EXPOSE 8080

ADD build/libs/tabooDrug-0.0.1-SNAPSHOT.jar tabooDrug-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","tabooDrug-0.0.1-SNAPSHOT.jar"]