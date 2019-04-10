FROM springcloud/openjdk:latest

ARG JAR_FILE

ADD target/${JAR_FILE} carisa-administration.jar

ENTRYPOINT exec java -Djava.security.egd=file:/dev/./urandom -jar /carisa-administration.jar
