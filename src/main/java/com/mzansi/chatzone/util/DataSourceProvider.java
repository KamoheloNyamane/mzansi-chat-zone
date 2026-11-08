package com.mzansi.chatzone.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Resolves the connection-pooled {@link DataSource} that Tomcat exposes
 * under {@code java:comp/env/jdbc/MzansiChatZoneDB} (configured in
 * META-INF/context.xml). Looked up once and cached — JNDI lookups are not
 * free, and every DAO method needs a connection.
 *
 * <p>Using a container-managed pool instead of opening a raw
 * {@code DriverManager} connection per request is the difference between
 * "works on my machine" and something that survives concurrent users.</p>
 */
public final class DataSourceProvider {

    private static volatile DataSource dataSource;

    private DataSourceProvider() {
    }

    public static DataSource get() {
        DataSource result = dataSource;
        if (result == null) {
            synchronized (DataSourceProvider.class) {
                result = dataSource;
                if (result == null) {
                    try {
                        Context initContext = new InitialContext();
                        Context envContext = (Context) initContext.lookup("java:comp/env");
                        result = (DataSource) envContext.lookup("jdbc/MzansiChatZoneDB");
                        dataSource = result;
                    } catch (NamingException e) {
                        throw new IllegalStateException(
                                "Could not resolve jdbc/MzansiChatZoneDB — check that META-INF/context.xml "
                                        + "is deployed and the MySQL credentials in it are correct.", e);
                    }
                }
            }
        }
        return result;
    }
}
