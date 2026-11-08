# Stage 1: Build application using Maven
FROM maven:3.9-eclipse-temurin-11 AS builder

WORKDIR /build
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Runtime environment using official Apache Tomcat 9
FROM tomcat:9.0-jdk11-temurin

# Remove default ROOT application
RUN rm -rf /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/ROOT.war

# Copy compiled WAR as ROOT.war so it serves directly at the domain root (/)
COPY --from=builder /build/target/mzansi-chat-zone.war /usr/local/tomcat/webapps/ROOT.war

# Ensure MySQL connector is in Tomcat's lib for container-managed JNDI pooling
COPY --from=builder /build/target/mzansi-chat-zone/WEB-INF/lib/mysql-connector-java-*.jar /usr/local/tomcat/lib/

# Create uploads directory
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/uploads

# Copy container entrypoint script
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
