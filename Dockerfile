FROM openjdk:8-jdk as builder
RUN mkdir -p /project
WORKDIR /project
COPY . /project
RUN ./gradlew build -x test

FROM openjdk:8-jre-alpine

RUN mkdir -p /srv/
WORKDIR /srv/
COPY --from=builder /project/build/libs/*.jar /srv/plugin-vimdriver-k8s-harbor.jar
RUN mkdir -p /var/log/openbaton
ENV RABBITMQ localhost
ENV RABBITMQ_PORT 5672
ENV CONSUMERS 5

ENTRYPOINT ["sh", "-c", "java -jar plugin-vimdriver-k8s-harbor.jar kubernetes $RABBITMQ $RABBITMQ_PORT $CONSUMERS"]
