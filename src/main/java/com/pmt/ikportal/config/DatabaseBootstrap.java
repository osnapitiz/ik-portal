package com.pmt.ikportal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Uygulama acilirken hedef veritabani yoksa onu olusturur.
 * Boylece PostgreSQL kurulu olan bir makinede ek bir el islemi gerekmez.
 * Tablolar daha sonra Hibernate (ddl-auto=update) tarafindan olusturulur.
 */
public class DatabaseBootstrap implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final Logger log = LoggerFactory.getLogger(DatabaseBootstrap.class);

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment env = event.getEnvironment();

        String url = env.getProperty("spring.datasource.url");
        String user = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");

        if (url == null || !url.startsWith("jdbc:postgresql://")) {
            return;
        }

        int lastSlash = url.lastIndexOf('/');
        if (lastSlash < 0) {
            return;
        }
        String serverPart = url.substring(0, lastSlash);
        String dbPart = url.substring(lastSlash + 1);
        int queryIndex = dbPart.indexOf('?');
        String database = queryIndex < 0 ? dbPart : dbPart.substring(0, queryIndex);

        if (database.isBlank() || "postgres".equals(database)) {
            return;
        }

        try (Connection connection = DriverManager.getConnection(serverPart + "/postgres", user, password)) {
            if (databaseExists(connection, database)) {
                return;
            }
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE DATABASE \"" + database.replace("\"", "\"\"") + "\"");
            }
            log.info("Veritabani olusturuldu: {}", database);
        } catch (Exception e) {
            log.warn("Veritabani otomatik olusturulamadi ({}). Gerekirse elle olusturun: CREATE DATABASE {};",
                    e.getMessage(), database);
        }
    }

    private boolean databaseExists(Connection connection, String database) throws Exception {
        try (PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?")) {
            ps.setString(1, database);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
