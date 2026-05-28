package edu.nju.di.integration.crossselect;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class CrossSelectConfig {
    public final int serverPort;
    public final String deptACrossSelectUrl;
    public final String deptBCrossSelectUrl;
    public final String deptCCrossSelectUrl;
    public final Path xsltDir;

    private CrossSelectConfig(int serverPort,
                              String deptACrossSelectUrl,
                              String deptBCrossSelectUrl,
                              String deptCCrossSelectUrl,
                              Path xsltDir) {
        this.serverPort = serverPort;
        this.deptACrossSelectUrl = deptACrossSelectUrl;
        this.deptBCrossSelectUrl = deptBCrossSelectUrl;
        this.deptCCrossSelectUrl = deptCCrossSelectUrl;
        this.xsltDir = xsltDir;
    }

    public static CrossSelectConfig load() throws IOException {
        Properties props = new Properties();

        Path external = Path.of("integration-crossselect.properties");
        if (Files.exists(external)) {
            try (InputStream in = Files.newInputStream(external)) {
                props.load(in);
            }
        } else {
            try (InputStream in = CrossSelectConfig.class.getClassLoader()
                    .getResourceAsStream("integration-crossselect.properties")) {
                if (in == null) {
                    throw new IOException("缺少 integration-crossselect.properties（工作目录或classpath均未找到）");
                }
                props.load(in);
            }
        }

        int port = Integer.parseInt(require(props, "server.port"));
        // B为成员2主负责院系，默认要求提供；A/C可选（仅在targetDept=A/C时需要）
        String deptAUrl = optional(props, "deptA.crossSelectUrl");
        String deptBUrl = require(props, "deptB.crossSelectUrl");
        String deptCUrl = optional(props, "deptC.crossSelectUrl");
        Path xsltDir = Path.of(require(props, "xslt.dir"));

        return new CrossSelectConfig(port, deptAUrl, deptBUrl, deptCUrl, xsltDir);
    }

    private static String require(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("配置缺失: " + key);
        }
        return value.trim();
    }

    private static String optional(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
