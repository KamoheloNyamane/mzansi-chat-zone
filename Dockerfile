# Stage 1: Build application using Maven
FROM maven:3.9-eclipse-temurin-11 AS builder

WORKDIR /build
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Runtime environment using official Apache Tomcat 9
FROM tomcat:9.0-jdk11-temurin

# Remove all default webapps to prevent any conflicts
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the exploded application directly as ROOT so it serves immediately from /
COPY --from=builder /build/target/mzansi-chat-zone /usr/local/tomcat/webapps/ROOT

# Ensure MySQL connector is in Tomcat's lib for container-managed JNDI pooling
COPY --from=builder /build/target/mzansi-chat-zone/WEB-INF/lib/mysql-connector-java-*.jar /usr/local/tomcat/lib/

# Ensure uploads directory exists and is writable
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/uploads

# Disable shutdown port to prevent Render health checks from sending invalid shutdown commands
RUN sed -i 's/port="8005"/port="-1"/' /usr/local/tomcat/conf/server.xml

# Copy container entrypoint script
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
