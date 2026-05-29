package edu.nju.di.integration.server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class IntegrationConfig {
    public final int serverPort;
    public final Path xsltDir;

    public final String deptABaseUrl;
    public final String deptBBaseUrl;
    public final String deptCBaseUrl;

    public final String deptACrossSelectUrl;
    public final String deptBCrossSelectUrl;
    public final String deptCCrossSelectUrl;

    public final String deptACrossDropUrl;
    public final String deptBCrossDropUrl;
    public final String deptCCrossDropUrl;

    private IntegrationConfig(int serverPort, Path xsltDir,
                              String deptABaseUrl, String deptBBaseUrl, String deptCBaseUrl,
                              String deptACrossSelectUrl, String deptBCrossSelectUrl, String deptCCrossSelectUrl,
                              String deptACrossDropUrl, String deptBCrossDropUrl, String deptCCrossDropUrl) {
        this.serverPort = serverPort;
        this.xsltDir = xsltDir;
        this.deptABaseUrl = deptABaseUrl;
        this.deptBBaseUrl = deptBBaseUrl;
        this.deptCBaseUrl = deptCBaseUrl;
        this.deptACrossSelectUrl = deptACrossSelectUrl;
        this.deptBCrossSelectUrl = deptBCrossSelectUrl;
        this.deptCCrossSelectUrl = deptCCrossSelectUrl;
        this.deptACrossDropUrl = deptACrossDropUrl;
        this.deptBCrossDropUrl = deptBCrossDropUrl;
        this.deptCCrossDropUrl = deptCCrossDropUrl;
    }

    public static IntegrationConfig load() throws IOException {
        Properties props = new Properties();

        Path external = Path.of("integration-server.properties");
        if (Files.exists(external)) {
            try (InputStream in = Files.newInputStream(external)) {
                props.load(in);
            }
        } else {
            try (InputStream in = IntegrationConfig.class.getClassLoader()
                    .getResourceAsStream("integration-server.properties")) {
                if (in == null) {
                    throw new IOException("缺少 integration-server.properties（工作目录或classpath均未找到）");
                }
                props.load(in);
            }
        }

        int port = Integer.parseInt(require(props, "server.port"));
        Path xsltDir = Path.of(require(props, "xslt.dir"));

        String aBase = optional(props, "deptA.baseUrl");
        String bBase = optional(props, "deptB.baseUrl");
        String cBase = optional(props, "deptC.baseUrl");

        String aSelect = optional(props, "deptA.crossSelectUrl");
        String bSelect = optional(props, "deptB.crossSelectUrl");
        String cSelect = optional(props, "deptC.crossSelectUrl");

        String aDrop = optional(props, "deptA.crossDropUrl");
        String bDrop = optional(props, "deptB.crossDropUrl");
        String cDrop = optional(props, "deptC.crossDropUrl");

        return new IntegrationConfig(port, xsltDir, aBase, bBase, cBase, aSelect, bSelect, cSelect, aDrop, bDrop, cDrop);
    }

    public String resolveBaseUrl(String dept) {
        return switch (dept.toUpperCase()) {
            case "A" -> deptABaseUrl;
            case "B" -> deptBBaseUrl;
            case "C" -> deptCBaseUrl;
            default -> null;
        };
    }

    public String resolveCrossSelectUrl(String dept) {
        return switch (dept.toUpperCase()) {
            case "A" -> deptACrossSelectUrl;
            case "B" -> deptBCrossSelectUrl;
            case "C" -> deptCCrossSelectUrl;
            default -> null;
        };
    }

    public String resolveCrossDropUrl(String dept) {
        return switch (dept.toUpperCase()) {
            case "A" -> deptACrossDropUrl;
            case "B" -> deptBCrossDropUrl;
            case "C" -> deptCCrossDropUrl;
            default -> null;
        };
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
