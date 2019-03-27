FROM openjdk:8-jdk-alpine
VOLUME /tmp
RUN apk add --update \
    curl \
    && rm -rf /var/cache/apk/*
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
EXPOSE 8061
ADD wait-for-config.sh /tmp
ENTRYPOINT ["./tmp/wait-for-config.sh","config-service","java","-cp","app:app/lib/*","com.cloud.ecomerce.discovery.EurekaServiceApplication"]