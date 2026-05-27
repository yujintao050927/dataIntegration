package edu.nju.di.deptb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DbProvider {
    private final DeptBConfig config;

    public DbProvider(DeptBConfig config) {
        this.config = config;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(config.dbUrl, config.dbUser, config.dbPassword);
    }
}
