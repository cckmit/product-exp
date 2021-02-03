#Dockerfile (create file in the project directory)
FROM nexus.tmbbank.local:60021/openjdk:11
EXPOSE 80
ADD *.jar app.jar
ENTRYPOINT ["java", "-Duser.timezone=GMT+7", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
