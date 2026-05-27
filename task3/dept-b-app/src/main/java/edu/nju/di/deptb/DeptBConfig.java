package edu.nju.di.deptb;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class DeptBConfig {
    public final int serverPort;
    public final String dbUrl;
    public final String dbUser;
    public final String dbPassword;

    private DeptBConfig(int serverPort, String dbUrl, String dbUser, String dbPassword) {
        this.serverPort = serverPort;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    public static DeptBConfig load() throws IOException {
        Properties props = new Properties();

        Path external = Path.of("deptb.properties");
        if (Files.exists(external)) {
            try (InputStream in = Files.newInputStream(external)) {
                props.load(in);
            }
        } else {
            try (InputStream in = DeptBConfig.class.getClassLoader().getResourceAsStream("deptb.properties")) {
                if (in == null) {
                    throw new IOException("缺少 deptb.properties（工作目录或classpath均未找到）");
                }
                props.load(in);
            }
        }

        int port = Integer.parseInt(require(props, "server.port"));
        String url = require(props, "db.url");
        String user = require(props, "db.user");
        String pwd = require(props, "db.password");

        return new DeptBConfig(port, url, user, pwd);
    }

    private static String require(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("配置缺失: " + key);
        }
        return value.trim();
    }
}
