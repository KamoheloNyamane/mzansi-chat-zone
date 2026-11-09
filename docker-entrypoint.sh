#!/bin/sh
set -e

DB_HOST=${DB_HOST:-localhost}
DB_PORT=${DB_PORT:-3306}
DB_NAME=${DB_NAME:-test}
DB_USER=${DB_USER:-root}
DB_PASSWORD=${DB_PASSWORD:-}
DB_SSL_MODE=${DB_SSL_MODE:-PREFERRED}

if [ "$DB_SSL_MODE" = "REQUIRED" ] || [ "$DB_SSL_MODE" = "true" ]; then
    SSL_PARAM="useSSL=true&amp;sslMode=REQUIRED&amp;allowPublicKeyRetrieval=true"
else
    SSL_PARAM="useSSL=false&amp;allowPublicKeyRetrieval=true"
fi

JDBC_URL="jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?${SSL_PARAM}&amp;serverTimezone=UTC&amp;characterEncoding=UTF-8"

# If Render specifies a custom port via $PORT, update the connector port dynamically
HTTP_PORT=${PORT:-8080}
sed -i "s/port=\"8080\"/port=\"${HTTP_PORT}\"/" /usr/local/tomcat/conf/server.xml

# Remove internal context.xml from webapp so it doesn't override cloud configuration
rm -f /usr/local/tomcat/webapps/ROOT/META-INF/context.xml

mkdir -p /usr/local/tomcat/conf/Catalina/localhost

# Write the container-managed JNDI DataSource configuration for ROOT
cat <<EOF > /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <Resource name="jdbc/MzansiChatZoneDB"
              auth="Container"
              type="javax.sql.DataSource"
              driverClassName="com.mysql.cj.jdbc.Driver"
              url="${JDBC_URL}"
              username="${DB_USER}"
              password="${DB_PASSWORD}"
              maxTotal="20"
              maxIdle="10"
              minIdle="2"
              maxWaitMillis="10000"
              testOnBorrow="true"
              validationQuery="SELECT 1"/>
</Context>
EOF

exec catalina.sh run
