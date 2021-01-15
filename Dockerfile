FROM openjdk:17-slim
MAINTAINER michael@mikuger.de

COPY ./build/libs/SmartHomeDataCollector-0.2.x.jar /home/javarun/app.jar

ENTRYPOINT ["java", "-jar", "/home/javarun/app.jar"]