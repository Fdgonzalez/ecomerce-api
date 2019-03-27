FROM openjdk:8-jdk-alpine
RUN apk add --update \
    curl \
    && rm -rf /var/cache/apk/*
VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
EXPOSE 8092
ADD wait-for-config.sh /tmp
ENTRYPOINT ["./tmp/wait-for-config.sh","config-service","java","-cp","app:app/lib/*","com.cloud.ecomerce.cart.CartService"]