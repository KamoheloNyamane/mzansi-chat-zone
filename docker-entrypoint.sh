#!/bin/sh
set -e

DB_HOST=${DB_HOST:-localhost}
DB_PORT=${DB_PORT:-3306}
DB_NAME=${DB_NAME:-mzansi_chat_zone}
DB_USER=${DB_USER:-root}
DB_PASSWORD=${DB_PASSWORD:-}
DB_SSL_MODE=${DB_SSL_MODE:-PREFERRED}

if [ "$DB_SSL_MODE" = "REQUIRED" ] || [ "$DB_SSL_MODE" = "true" ]; then
    SSL_PARAM="useSSL=true&amp;requireSSL=true"
else
    SSL_PARAM="useSSL=false"
fi

JDBC_URL="jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?${SSL_PARAM}&amp;serverTimezone=UTC&amp;allowPublicKeyRetrieval=true&amp;characterEncoding=UTF-8"

mkdir -p /usr/local/tomcat/conf/Catalina/localhost

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
