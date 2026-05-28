package edu.nju.di.deptc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DbProvider {
    private final DeptCConfig config;

    public DbProvider(DeptCConfig config) {
        this.config = config;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(config.dbUrl, config.dbUser, config.dbPassword);
    }
}
