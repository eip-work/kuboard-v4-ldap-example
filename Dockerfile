
FROM  docker.m.daocloud.io/maven:3.8.5-openjdk-17 AS builder
ARG KUBOARD_LDAP_EXAMPLE_VERSION="0.0.1-SNAPSHOT"

ENV TZ="Asia/Shanghai"

WORKDIR /app

COPY ./src /app/src
COPY ./pom.xml /app/pom.xml

RUN mvn -Drevision=${KUBOARD_LDAP_EXAMPLE_VERSION} -DskipTests=true clean package

FROM docker.m.daocloud.io/openjdk:17-ea-33-slim
ARG KUBOARD_LDAP_EXAMPLE_VERSION="0.0.1-SNAPSHOT"

WORKDIR /app
ENV TZ="Asia/Shanghai"
EXPOSE 9090

COPY --from=builder /app/target/kuboard-v4-ldap-example-${KUBOARD_LDAP_EXAMPLE_VERSION}.jar /app/kuboard-v4-ldap-example.jar

ENTRYPOINT ["java", "-jar", "kuboard-v4-ldap-example.jar"]