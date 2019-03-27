FROM openjdk:8-jdk-alpine
VOLUME /tmp
RUN apk add --update \
    curl \
    && rm -rf /var/cache/apk/*
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
EXPOSE 8088
ENTRYPOINT ["java","-cp","app:app/lib/*","com.cloud.ecomerce.configservice.ConfigService"]