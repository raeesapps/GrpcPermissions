package net.raees;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class RbacDataStore {
    private static final String PERMISSIONS_POSTGRES_PASSWORD_ENV_VAR = "PERMISSIONS_POSTGRES_PASSWORD";

    private final BasicDataSource dataSource = new BasicDataSource();

    RbacDataStore(String url, String user) {
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(System.getenv(PERMISSIONS_POSTGRES_PASSWORD_ENV_VAR));
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setMaxOpenPreparedStatements(100);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
