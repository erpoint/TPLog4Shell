FROM log4shell:java

COPY ./ /app
WORKDIR /app

RUN mvn clean package

VOLUME /app/shared

EXPOSE 8200
EXPOSE 9999

CMD ["java", "-jar", "target/log4shell-server-0.0.1-SNAPSHOT-jar-with-dependencies.jar"]

