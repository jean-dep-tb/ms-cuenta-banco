FROM openjdk:8
VOLUME /tmp
EXPOSE 8021
ADD ./target/spring-boot-webflu-ms-cuenta-banco-0.0.1-SNAPSHOT.jar ms.cuenta.banco.jar
ENTRYPOINT ["java","-jar","/ms.cuenta.banco.jar"]