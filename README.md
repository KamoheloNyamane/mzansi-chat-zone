# Mzansi Chat Zone 💬🇿🇦

Mzansi Chat Zone is a production-grade, real-time messaging web application built with Java Servlets (Java EE / Jakarta EE 8 `javax.*` namespace), the JSR 356 WebSocket API, and MySQL. It features secure user authentication, 1-on-1 real-time chat, HTML5 camera photo capture and sharing, and container-managed JNDI connection pooling on Apache Tomcat 9.

---

## 🌟 Key Features

- **Real-Time Messaging**: Built on the JSR 356 WebSocket API (`@ServerEndpoint("/chat")`) for instantaneous message transmission.
- **In-Browser Camera Integration**: Live photo capture via HTML5 `navigator.mediaDevices.getUserMedia` and canvas snapshotting, posted via HTTP multipart directly into conversations.
- **Enterprise Security**:
  - Passwords hashed using industry-standard **BCrypt** (work factor 12).
  - Session fixation protection on authentication (`session.invalidate()`).
  - Cross-cutting `AuthenticationFilter` guarding all authenticated routes.
  - Contextual XSS sanitization (`HtmlEscaper`).
  - Authorization / IDOR checks preventing users from accessing or posting into conversations they do not belong to.
- **High Performance & Connection Pooling**: JNDI connection pooling managed by Apache Tomcat (`Resource` DataSource) ensuring low-latency database queries without opening raw JDBC connections per request.
- **Modern Responsive UI**: Mobile-first, responsive layouts designed with clean CSS variables and typography.

---

## 🏗️ Technology Stack

- **Backend**: Java 11 / Java 26, Java Servlet 4.0 (`javax.servlet-api`), JSR 356 WebSocket (`javax.websocket-api`).
- **Password Security**: `at.favre.lib:bcrypt` (v0.10.2).
- **JSON Serialization**: `org.json:json`.
- **Database**: MySQL 8 / MariaDB (via `mysql-connector-j`).
- **Server**: Apache Tomcat 9.0.x (javax namespace).
- **Frontend**: JSP, vanilla JavaScript (ES6+), HTML5 Canvas & MediaDevices API, CSS3.
- **Build Tool**: Apache Maven (`war` packaging).

---

## 🗄️ Database Setup

1. Start MySQL (e.g. through XAMPP or native service).
2. Execute the provided [`schema.sql`](schema.sql) in your MySQL console or phpMyAdmin:
   ```sql
   source schema.sql;
   ```
   This creates the `mzansi_chat_zone` database with all tables (`users`, `conversations`, `conversation_participants`, `messages`) and default seed users.

### Default Seed Accounts
| Username | Default Password | Role |
| :--- | :--- | :--- |
| `thanyani` | `Password123!` | Standard User |
| `fulu.makgoka` | `Password123!` | Standard User |
| `maanda.n` | `Password123!` | Standard User |
| `lenah.eduvos` | `Password123!` | Standard User |

---

## 🚀 Local Development Setup (NetBeans & Tomcat 9)

1. **MySQL Driver in Tomcat**: Ensure `mysql-connector-j-8.0.33.jar` is placed in Tomcat's `lib/` directory (`apache-tomcat-9.0.x/lib`).
2. **Context Configuration**: Check [`src/main/webapp/META-INF/context.xml`](src/main/webapp/META-INF/context.xml) to confirm database credentials:
   ```xml
   <Context path="/mzansi-chat-zone">
       <Resource name="jdbc/MzansiChatZoneDB"
                 auth="Container"
                 type="javax.sql.DataSource"
                 driverClassName="com.mysql.cj.jdbc.Driver"
                 url="jdbc:mysql://localhost:3306/mzansi_chat_zone?useSSL=false&amp;serverTimezone=UTC&amp;allowPublicKeyRetrieval=true&amp;characterEncoding=UTF-8"
                 username="root"
                 password=""
                 maxTotal="20"
                 maxIdle="10"
                 minIdle="2"
                 maxWaitMillis="10000"
                 testOnBorrow="true"
                 validationQuery="SELECT 1"/>
   </Context>
   ```
3. **Build**:
   In NetBeans, right-click the project and select **Clean and Build** (or run `mvn clean package`).
4. **Run**:
   Click **Run** in NetBeans. The application launches at:
   `http://localhost:8080/mzansi-chat-zone/`

---

## 🚢 Production & Cloud Hosting Guide

For production deployments:
1. **Containerized Deployment (Docker)**: Deploy a Tomcat 9 container (`tomcat:9-jdk11` or `tomcat:9-jdk17`) with the compiled `mzansi-chat-zone.war` placed in `/usr/local/tomcat/webapps/ROOT.war`.
2. **Database Cloud Services**: Host the database on managed MySQL (AWS RDS, PlanetScale, Railway, or DigitalOcean Managed MySQL).
3. **WebSocket Support**: Ensure the reverse proxy (Nginx, Traefik, AWS ALB, or Cloudflare) has WebSocket upgrading enabled (`Upgrade $http_upgrade`, `Connection "upgrade"`).
4. **Environment Variables**: Externalize credentials using environment variables or JNDI server configuration in production `conf/server.xml` or `conf/context.xml`.
