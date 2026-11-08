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
- **Database**: MySQL 8 / MariaDB / TiDB (via `mysql-connector-j`).
- **Server**: Apache Tomcat 9.0.x (javax namespace).
- **Frontend**: JSP, vanilla JavaScript (ES6+), HTML5 Canvas & MediaDevices API, CSS3.
- **Build Tool**: Apache Maven (`war` packaging).
- **Containerization**: Multi-stage Dockerfile (`maven:3.9` -> `tomcat:9.0-jdk11-temurin`).

---

## 🗄️ Database Setup (Local)

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
2. **Context Configuration**: Check [`src/main/webapp/META-INF/context.xml`](src/main/webapp/META-INF/context.xml) to confirm local database credentials.
3. **Build & Run**:
   In NetBeans, click **Clean and Build**, then **Run**. The application launches at:
   `http://localhost:8080/mzansi-chat-zone/`

---

## ☁️ Free Cloud Deployment (Render + TiDB Cloud)

You can host this application completely free of charge with **zero credit card required** using **Render** (free Docker Web Service) + **TiDB Cloud Starter** (free 5 GiB MySQL-compatible database).

### Step 1: Create Free Database on TiDB Cloud
1. Sign up for free at [tidbcloud.com](https://tidbcloud.com) (no credit card needed).
2. Create a new **Serverless / Starter** cluster (MySQL-compatible).
3. Under cluster settings, click **Connect**:
   - Note down: `Host`, `Port` (usually `4000`), `User` (format: `xxxx.root`), and your generated `Password`.
   - The default database is `test` (or create `mzansi_chat_zone`).
4. In the TiDB Cloud Web SQL Console, paste and execute the contents of [`schema.sql`](schema.sql) to create the tables and seed users.

### Step 2: Push Repository to GitHub
```bash
git remote add origin https://github.com/<your-username>/mzansi-chat-zone.git
git push -u origin main
```

### Step 3: Deploy on Render
1. Sign up at [render.com](https://render.com).
2. Click **New +** → **Blueprint** and select your GitHub repository `mzansi-chat-zone`.
3. Render will detect [`render.yaml`](render.yaml) and prompt for the environment variables:
   - `DB_HOST`: Your TiDB Host (e.g. `gateway01.eu-central-1.prod.aws.tidbcloud.com`)
   - `DB_PORT`: `4000`
   - `DB_NAME`: `test` (or `mzansi_chat_zone`)
   - `DB_USER`: Your TiDB user (e.g. `xxxx.root`)
   - `DB_PASSWORD`: Your TiDB generated password
   - `DB_SSL_MODE`: `REQUIRED`
4. Click **Apply**. Render will automatically build the multi-stage Docker container, initialize Tomcat 9, and issue a free SSL certificate (`https://your-service.onrender.com`).
5. Open your live HTTPS link: registration, live WebSocket chat, and camera photo capture are immediately operational!
