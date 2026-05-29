package edu.nju.di.integration.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.nju.di.integration.server.IntegrationConfig;
import edu.nju.di.integration.xslt.HttpXmlIO;
import edu.nju.di.integration.xslt.XsltTransformer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public final class StatisticsHandler implements HttpHandler {
    private final IntegrationConfig config;
    private final XsltTransformer xslt;
    private final HttpClient httpClient;

    public StatisticsHandler(IntegrationConfig config, XsltTransformer xslt) {
        this.config = config;
        this.xslt = xslt;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod()) && !"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpXmlIO.writeXmlResponseUtf8(exchange, 405, errorXml("只支持GET或POST"));
            return;
        }

        try {
            String responseXml = process();
            HttpXmlIO.writeXmlResponseUtf8(exchange, 200, responseXml);
        } catch (Exception e) {
            HttpXmlIO.writeXmlResponseUtf8(exchange, 500, errorXml("统计失败: " + e.getMessage()));
        }
    }

    private String process() throws IOException, InterruptedException, TransformerException, DocumentException {
        int totalStudents = 0;
        int totalCourses = 0;
        int totalChoices = 0;
        StringBuilder detail = new StringBuilder();

        for (String dept : List.of("A", "B", "C")) {
            String baseUrl = config.resolveBaseUrl(dept);
            if (baseUrl == null || baseUrl.isBlank()) {
                detail.append("<dept name=\"").append(dept).append("\" status=\"未配置\"/>");
                continue;
            }

            int stuCount = 0;
            int crsCount = 0;
            int chCount = 0;
            String status = "ok";

            try {
                String stuXml = fetchXml(baseUrl + "/students");
                if (stuXml != null) {
                    String unified = xslt.transformXml(stuXml, "formatStudent.xsl");
                    stuCount = countElements(unified, "student");
                    totalStudents += stuCount;
                }

                String crsXml = fetchXml(baseUrl + "/courses");
                if (crsXml != null) {
                    String unified = xslt.transformXml(crsXml, "formatClass.xsl");
                    crsCount = countElements(unified, "class");
                    totalCourses += crsCount;
                }

                String chXml = fetchXml(baseUrl + "/choices");
                if (chXml != null) {
                    String unified = xslt.transformXml(chXml, "formatClassChoice.xsl");
                    chCount = countElements(unified, "choice");
                    totalChoices += chCount;
                }
            } catch (Exception e) {
                status = "error: " + escapeXml(e.getMessage());
            }

            detail.append("<dept name=\"").append(dept)
                    .append("\" students=\"").append(stuCount)
                    .append("\" courses=\"").append(crsCount)
                    .append("\" choices=\"").append(chCount)
                    .append("\" status=\"").append(status).append("\"/>");
        }

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<statistics>\n" +
                "  <summary>\n" +
                "    <totalStudents>" + totalStudents + "</totalStudents>\n" +
                "    <totalCourses>" + totalCourses + "</totalCourses>\n" +
                "    <totalChoices>" + totalChoices + "</totalChoices>\n" +
                "  </summary>\n" +
                "  <details>\n" +
                "    " + detail.toString() + "\n" +
                "  </details>\n" +
                "</statistics>";
    }

    private String fetchXml(String url) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(8))
                .header("Accept", "application/xml")
                .GET()
                .build();
        try {
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 400) {
                System.err.println("[statistics] 请求失败 " + url + " -> HTTP " + resp.statusCode());
                return null;
            }
            return resp.body();
        } catch (Exception e) {
            System.err.println("[statistics] 请求异常 " + url + ": " + e.getMessage());
            return null;
        }
    }

    private int countElements(String xml, String localName) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document doc = reader.read(new StringReader(xml));
        List<?> list = doc.selectNodes("//*[local-name()='" + localName + "']");
        return list.size();
    }

    private static String errorXml(String msg) {
        String safe = escapeXml(msg == null ? "" : msg);
        return "<statistics><success>false</success><message>" + safe + "</message></statistics>";
    }

    private static String escapeXml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
