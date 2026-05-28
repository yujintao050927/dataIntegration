package edu.nju.di.integration.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.nju.di.integration.server.IntegrationConfig;
import edu.nju.di.integration.xslt.HttpXmlIO;
import edu.nju.di.integration.xslt.XsltTransformer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
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

public final class CrossSelectHandler implements HttpHandler {
    private final IntegrationConfig config;
    private final XsltTransformer xslt;
    private final HttpClient httpClient;

    public CrossSelectHandler(IntegrationConfig config, XsltTransformer xslt) {
        this.config = config;
        this.xslt = xslt;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpXmlIO.writeXmlResponseUtf8(exchange, 405, errorXml("只支持POST"));
            return;
        }

        String requestXml = HttpXmlIO.readRequestBodyUtf8(exchange);
        try {
            String responseXml = process(requestXml);
            HttpXmlIO.writeXmlResponseUtf8(exchange, 200, responseXml);
        } catch (Exception e) {
            HttpXmlIO.writeXmlResponseUtf8(exchange, 500, errorXml("跨系选课失败: " + e.getMessage()));
        }
    }

    private String process(String requestXml) throws DocumentException, IOException, TransformerException, InterruptedException {
        Document reqDoc = readXml(requestXml);
        Element root = reqDoc.getRootElement();

        String targetDept = textOfFirst(root, "targetDept");
        if (targetDept == null || targetDept.isBlank()) {
            targetDept = "C";
        }
        targetDept = targetDept.trim().toUpperCase();
        if (!"A".equals(targetDept) && !"B".equals(targetDept) && !"C".equals(targetDept)) {
            throw new IllegalArgumentException("不支持的targetDept: " + targetDept + "（仅支持A/B/C）");
        }

        Element studentEl = (Element) root.selectSingleNode("//*[local-name()='student']");
        Element classEl = (Element) root.selectSingleNode("//*[local-name()='class']");
        if (studentEl == null || classEl == null) {
            throw new IllegalArgumentException("请求缺少 student 或 class 元素");
        }

        // 源院系格式 -> 统一格式
        String unifiedStudentXml = xslt.transformXml(wrap("students", studentEl.asXML()), "formatStudent.xsl");
        String unifiedClassXml = xslt.transformXml(wrap("classes", classEl.asXML()), "formatClass.xsl");

        Document uStuDoc = readXml(unifiedStudentXml);
        Document uClsDoc = readXml(unifiedClassXml);

        Element uStudent = (Element) uStuDoc.selectSingleNode("//*[local-name()='student']");
        Element uClass = (Element) uClsDoc.selectSingleNode("//*[local-name()='class']");
        if (uStudent == null || uClass == null) {
            throw new IllegalArgumentException("统一格式转换失败：未得到 student/class");
        }

        String sid = textOfFirst(uStudent, "id");
        String cid = textOfFirst(uClass, "id");
        if (sid == null || sid.isBlank() || cid == null || cid.isBlank()) {
            throw new IllegalArgumentException("统一格式缺少 id：sid/cid为空");
        }

        // 统一选课对象
        String unifiedChoiceXml = "<choices><choice><sid>" + escapeXml(sid) + "</sid><cid>" + escapeXml(cid) + "</cid><score></score></choice></choices>";

        // 统一格式 -> 目标院系格式
        String studentXslt = "studentTo" + targetDept + ".xsl";
        String classXslt = "classTo" + targetDept + ".xsl";
        String choiceXslt = "choiceTo" + targetDept + ".xsl";

        String tStudentXml = xslt.transformXml(unifiedStudentXml, studentXslt);
        String tClassXml = xslt.transformXml(unifiedClassXml, classXslt);
        String tChoiceXml = xslt.transformXml(unifiedChoiceXml, choiceXslt);

        Element tStudent = (Element) readXml(tStudentXml).selectSingleNode("//*[local-name()='student']");
        Element tClass = (Element) readXml(tClassXml).selectSingleNode("//*[local-name()='class']");
        Element tChoice = (Element) readXml(tChoiceXml).selectSingleNode("//*[local-name()='choice']");
        if (tStudent == null || tClass == null || tChoice == null) {
            throw new IllegalArgumentException("转换到目标院系格式失败(targetDept=" + targetDept + "): 缺少 student/class/choice");
        }

        String forwardUrl = resolveForwardUrl(targetDept);
        if (forwardUrl == null) {
            throw new IllegalArgumentException("未配置目标院系的crossSelectUrl: " + targetDept);
        }

        String forwardXml = "<crossSelectRequest>" +
            tStudent.asXML() +
            tClass.asXML() +
            tChoice.asXML() +
            "</crossSelectRequest>";

        HttpRequest forwardReq = HttpRequest.newBuilder()
                .uri(URI.create(forwardUrl))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/xml; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(forwardXml, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp = httpClient.send(forwardReq, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() >= 400) {
            throw new IOException("目标院系返回HTTP " + resp.statusCode() + ": " + resp.body());
        }
        return resp.body();
    }

    private String resolveForwardUrl(String targetDept) {
        return config.resolveCrossSelectUrl(targetDept);
    }

    private static Document readXml(String xml) throws DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(new StringReader(xml));
    }

    private static String wrap(String rootName, String innerXml) {
        return "<" + rootName + ">" + innerXml + "</" + rootName + ">";
    }

    private static String textOfFirst(Element parent, String childLocalName) {
        Element el = (Element) parent.selectSingleNode("./*[local-name()='" + childLocalName + "']");
        if (el == null) {
            return null;
        }
        return el.getTextTrim();
    }

    private static String errorXml(String msg) {
        String safe = escapeXml(msg == null ? "" : msg);
        return "<crossSelectResponse><success>false</success><message>" + safe + "</message></crossSelectResponse>";
    }

    private static String escapeXml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
