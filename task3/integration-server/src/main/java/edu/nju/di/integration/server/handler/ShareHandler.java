package edu.nju.di.integration.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.nju.di.integration.server.IntegrationConfig;
import edu.nju.di.integration.xslt.HttpXmlIO;
import edu.nju.di.integration.xslt.XsltTransformer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
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
import java.util.ArrayList;
import java.util.List;

public final class ShareHandler implements HttpHandler {
    private final IntegrationConfig config;
    private final XsltTransformer xslt;
    private final HttpClient httpClient;

    public ShareHandler(IntegrationConfig config, XsltTransformer xslt) {
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
            HttpXmlIO.writeXmlResponseUtf8(exchange, 500, errorXml("课程共享失败: " + e.getMessage()));
        }
    }

    private String process(String requestXml) throws DocumentException, IOException, TransformerException, InterruptedException {
        Document reqDoc = readXml(requestXml);
        Element root = reqDoc.getRootElement();
        String requester = textOfFirst(root, "requester");
        if (requester == null || requester.isBlank()) {
            requester = "C";
        }
        requester = requester.trim().toUpperCase();
        if (!"A".equals(requester) && !"B".equals(requester) && !"C".equals(requester)) {
            throw new IllegalArgumentException("不支持的requester: " + requester);
        }

        // 收集其他院系的共享课程（统一格式），并记录 id -> sourceDept 映射
        List<Element> sharedClasses = new ArrayList<>();
        java.util.Map<String, String> idToSource = new java.util.HashMap<>();

        for (String dept : List.of("A", "B", "C")) {
            if (dept.equals(requester)) continue;
            String baseUrl = config.resolveBaseUrl(dept);
            if (baseUrl == null || baseUrl.isBlank()) {
                continue; // 未配置则跳过
            }
            String coursesXml = fetchXml(baseUrl + "/courses");
            if (coursesXml == null) continue;

            // 对C院系原始XML做共享过滤
            if ("C".equals(dept)) {
                coursesXml = filterSharedCoursesC(coursesXml);
                if (coursesXml == null) continue;
            }

            // 转为统一格式
            String unified = xslt.transformXml(coursesXml, "formatClass.xsl");
            Document uDoc = readXml(unified);
            List<?> clsList = uDoc.selectNodes("//*[local-name()='class']");
            for (Object o : clsList) {
                Element cls = (Element) o;
                String id = textOfFirst(cls, "id");
                if (id != null && !id.isBlank()) {
                    idToSource.put(id, dept);
                }
                sharedClasses.add(cls);
            }
        }

        // 组装统一格式XML
        Document unifiedDoc = DocumentHelper.createDocument();
        Element uRoot = unifiedDoc.addElement("classes");
        for (Element cls : sharedClasses) {
            uRoot.add(cls.createCopy());
        }

        // 统一格式 -> 请求者格式
        String targetXslt = "classTo" + requester + ".xsl";
        String result = xslt.transformXml(uRoot.asXML(), targetXslt);

        // 在目标格式结果中补回 sourceDept（根据课程id查找映射）
        result = injectSourceDept(result, idToSource);
        return result;
    }

    // 在目标格式XML中为每个class补回sourceDept
    private String injectSourceDept(String xml, java.util.Map<String, String> idToSource) throws DocumentException {
        Document doc = readXml(xml);
        List<?> nodes = doc.selectNodes("//*[local-name()='class']");
        for (Object o : nodes) {
            Element cls = (Element) o;
            // 尝试读取目标格式的课程号字段
            String cno = textOfFirst(cls, "Cno", "课程号", "id");
            if (cno != null && !cno.isBlank()) {
                String source = idToSource.get(cno);
                if (source != null) {
                    // 如果已存在则先移除
                    Element old = (Element) cls.selectSingleNode("./*[local-name()='sourceDept']");
                    if (old != null) old.detach();
                    cls.addElement("sourceDept").setText(source);
                }
            }
        }
        return doc.asXML();
    }

    // 对C院系的XML过滤：只保留share_flag='Y'的课程
    private String filterSharedCoursesC(String xml) throws DocumentException {
        Document doc = readXml(xml);
        Element root = doc.getRootElement();
        List<?> nodes = root.selectNodes("//*[local-name()='class']");
        List<Element> toRemove = new ArrayList<>();
        for (Object o : nodes) {
            Element cls = (Element) o;
            Element shareFlag = (Element) cls.selectSingleNode("./*[local-name()='share_flag']");
            if (shareFlag == null || !"Y".equalsIgnoreCase(shareFlag.getTextTrim())) {
                toRemove.add(cls);
            }
        }
        for (Element el : toRemove) {
            el.getParent().remove(el);
        }
        return root.asXML();
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
                System.err.println("[share] 请求失败 " + url + " -> HTTP " + resp.statusCode());
                return null;
            }
            return resp.body();
        } catch (Exception e) {
            System.err.println("[share] 请求异常 " + url + ": " + e.getMessage());
            return null;
        }
    }

    private static Document readXml(String xml) throws DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(new StringReader(xml));
    }

    private static String textOfFirst(Element parent, String... childLocalNames) {
        for (String name : childLocalNames) {
            Element el = (Element) parent.selectSingleNode("./*[local-name()='" + name + "']");
            if (el != null) {
                String t = el.getTextTrim();
                if (!t.isEmpty()) {
                    return t;
                }
            }
        }
        return null;
    }

    private static String errorXml(String msg) {
        String safe = escapeXml(msg == null ? "" : msg);
        return "<shareResponse><success>false</success><message>" + safe + "</message></shareResponse>";
    }

    private static String escapeXml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
