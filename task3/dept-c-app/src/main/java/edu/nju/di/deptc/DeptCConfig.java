package edu.nju.di.deptc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class DeptCConfig {
    public final int serverPort;
    public final String dbUrl;
    public final String dbUser;
    public final String dbPassword;

    private DeptCConfig(int serverPort, String dbUrl, String dbUser, String dbPassword) {
        this.serverPort = serverPort;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    public static DeptCConfig load() throws IOException {
        Properties props = new Properties();

        Path external = Paths.get("deptc.properties");
        if (Files.exists(external)) {
            try (InputStream in = Files.newInputStream(external)) {
                props.load(in);
            }
        } else {
            try (InputStream in = DeptCConfig.class.getClassLoader().getResourceAsStream("deptc.properties")) {
                if (in == null) {
                    throw new IOException("缺少 deptc.properties（工作目录或classpath均未找到）");
                }
                props.load(in);
            }
        }

        int port = Integer.parseInt(require(props, "server.port"));
        String url = require(props, "db.url");
        String user = require(props, "db.user");
        String pwd = props.getProperty("db.password");
        if (pwd == null) {
            pwd = "";
        }

        return new DeptCConfig(port, url, user, pwd);
    }

    private static String require(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("配置缺失: " + key);
        }
        return value.trim();
    }
}