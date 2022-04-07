FROM openjdk:11
ARG JAR_FILE=./build/libs/TNT_hadoop-0.0.1-SNAPSHOT
copy ${JAR_FILE} tnt.jar
ENTRYPOINT ["java", "-jar", "tnt.jar"]
EXPOSE 9990