package edu.nju.di.deptc;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import edu.nju.di.deptc.dao.ChoiceDao;
import edu.nju.di.deptc.dao.CourseDao;
import edu.nju.di.deptc.dao.StudentDao;
import edu.nju.di.deptc.model.Student;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;

public final class DeptCHttpServer {
    private static final Logger logger = LoggerFactory.getLogger(DeptCHttpServer.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final HttpServer server;
    private final int port;

    public DeptCHttpServer(int port,
                           DeptCXmlService xmlService,
                           StudentDao studentDao,
                           CourseDao courseDao,
                           ChoiceDao choiceDao) throws IOException {
        this.port = port;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server.setExecutor(Executors.newFixedThreadPool(8));

        server.createContext("/courses", exchange -> {
            String clientIp = getClientIp(exchange);
            logRequest(clientIp, "GET", "/courses");
            try {
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    logResponse(clientIp, "/courses", 405, "只支持GET");
                    HttpUtil.writeXml(exchange, 405, errorXml("只支持GET"));
                    return;
                }
                String xml = xmlService.exportCoursesXml();
                logResponse(clientIp, "/courses", 200, "成功返回课程列表");
                HttpUtil.writeXml(exchange, 200, xml);
            } catch (Exception e) {
                logError(clientIp, "/courses", e);
                HttpUtil.writeXml(exchange, 500, errorXml(e.getMessage()));
            }
        });

        server.createContext("/students", exchange -> {
            String clientIp = getClientIp(exchange);
            logRequest(clientIp, "GET", "/students");
            try {
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    logResponse(clientIp, "/students", 405, "只支持GET");
                    HttpUtil.writeXml(exchange, 405, errorXml("只支持GET"));
                    return;
                }
                String xml = xmlService.exportStudentsXml();
                logResponse(clientIp, "/students", 200, "成功返回学生列表");
                HttpUtil.writeXml(exchange, 200, xml);
            } catch (Exception e) {
                logError(clientIp, "/students", e);
                HttpUtil.writeXml(exchange, 500, errorXml(e.getMessage()));
            }
        });

        server.createContext("/choices", exchange -> {
            String clientIp = getClientIp(exchange);
            logRequest(clientIp, "GET", "/choices");
            try {
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    logResponse(clientIp, "/choices", 405, "只支持GET");
                    HttpUtil.writeXml(exchange, 405, errorXml("只支持GET"));
                    return;
                }
                String xml = xmlService.exportChoicesXml();
                logResponse(clientIp, "/choices", 200, "成功返回选课列表");
                HttpUtil.writeXml(exchange, 200, xml);
            } catch (Exception e) {
                logError(clientIp, "/choices", e);
                HttpUtil.writeXml(exchange, 500, errorXml(e.getMessage()));
            }
        });

        server.createContext("/crossSelect", new CrossSelectReceiver(studentDao, courseDao, choiceDao));
        server.createContext("/crossDrop", new CrossDropReceiver(choiceDao));
        
        logger.info("[dept-c] HTTP服务器初始化完成，端口: {}", port);
    }

    public void start() {
        server.start();
        logger.info("[dept-c] HTTP服务器已启动，监听端口: {}", port);
    }

    private static String getClientIp(HttpExchange exchange) {
        String remoteAddress = exchange.getRemoteAddress().toString();
        if (remoteAddress.contains("/")) {
            return remoteAddress.substring(1).split(":")[0];
        }
        return remoteAddress;
    }

    private static void logRequest(String clientIp, String method, String path) {
        logger.info("[{}] {} {} - 请求来自 {}", 
            LocalDateTime.now().format(TIME_FORMATTER), method, path, clientIp);
    }

    private static void logResponse(String clientIp, String path, int status, String message) {
        logger.info("[{}] {} - 响应状态: {}, 消息: {}", 
            LocalDateTime.now().format(TIME_FORMATTER), path, status, message);
    }

    private static void logError(String clientIp, String path, Exception e) {
        logger.error("[{}] {} - 处理异常: {}", 
            LocalDateTime.now().format(TIME_FORMATTER), path, e.getMessage(), e);
    }

    private static final class CrossSelectReceiver implements HttpHandler {
        private static final Logger handlerLogger = LoggerFactory.getLogger(CrossSelectReceiver.class);
        
        private final StudentDao studentDao;
        private final CourseDao courseDao;
        private final ChoiceDao choiceDao;

        private CrossSelectReceiver(StudentDao studentDao, CourseDao courseDao, ChoiceDao choiceDao) {
            this.studentDao = studentDao;
            this.courseDao = courseDao;
            this.choiceDao = choiceDao;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String clientIp = getClientIp(exchange);
            logRequest(clientIp, "POST", "/crossSelect");
            
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                handlerLogger.warn("[crossSelect] 不支持的HTTP方法: {}", exchange.getRequestMethod());
                logResponse(clientIp, "/crossSelect", 405, "只支持POST");
                HttpUtil.writeXml(exchange, 405, errorXml("只支持POST"));
                return;
            }

            String xml = HttpUtil.readUtf8(exchange);
            handlerLogger.debug("[crossSelect] 收到请求XML: {}", xml.length() > 500 ? xml.substring(0, 500) + "..." : xml);
            
            try {
                Document doc = readXml(xml);
                Element choiceEl = (Element) doc.selectSingleNode("//*[local-name()='choice']");
                
                if (choiceEl == null) {
                    throw new IllegalArgumentException("缺少choice元素");
                }

                String sno = text(choiceEl, "学号", "Sno", "sid", "id");
                String cno = text(choiceEl, "课程编号", "课程号", "Cno", "cid");
                
                handlerLogger.debug("[crossSelect] 解析到学号: {}, 课程号: {}", sno, cno);
                
                if (isBlank(sno) || isBlank(cno)) {
                    throw new IllegalArgumentException("choice缺少学号/课程编号");
                }

                // 如果学生信息缺失但请求里带了student，则补全学生
                if (studentDao.findById(sno) == null) {
                    handlerLogger.info("[crossSelect] 学生 {} 不存在，尝试从请求中补全信息", sno);
                    Element studentEl = (Element) doc.selectSingleNode("//*[local-name()='student']");
                    if (studentEl != null) {
                        String name = text(studentEl, "姓名", "Snm", "name");
                        String sex = text(studentEl, "性别", "Sex", "sex");
                        String sde = text(studentEl, "专业", "院系", "Sde", "major");
                        if (isBlank(name)) name = sno;
                        if (isBlank(sex)) sex = "男";
                        if (isBlank(sde)) sde = "计算机科学";
                        studentDao.insertIfAbsent(new Student(sno, name, sex, sde, "123456"));
                        handlerLogger.info("[crossSelect] 已创建学生: {} ({}, {}, {})", sno, name, sex, sde);
                    } else {
                        throw new IllegalArgumentException("学生不存在且请求中缺少student元素");
                    }
                }

                if (courseDao.findById(cno) == null) {
                    throw new IllegalArgumentException("课程不存在: " + cno);
                }

                try {
                    choiceDao.addChoice(sno, cno);
                    handlerLogger.info("[crossSelect] 选课成功: 学生 {} 选择课程 {}", sno, cno);
                } catch (Exception e) {
                    if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                        throw new IllegalArgumentException("重复选课: " + sno + "," + cno);
                    }
                    throw e;
                }

                logResponse(clientIp, "/crossSelect", 200, "选课成功");
                HttpUtil.writeXml(exchange, 200, okXml("选课成功"));
            } catch (Exception e) {
                handlerLogger.error("[crossSelect] 处理失败: {}", e.getMessage());
                logError(clientIp, "/crossSelect", e);
                HttpUtil.writeXml(exchange, 400, failXml(e.getMessage()));
            }
        }
    }

    private static final class CrossDropReceiver implements HttpHandler {
        private static final Logger handlerLogger = LoggerFactory.getLogger(CrossDropReceiver.class);
        
        private final ChoiceDao choiceDao;

        private CrossDropReceiver(ChoiceDao choiceDao) {
            this.choiceDao = choiceDao;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String clientIp = getClientIp(exchange);
            logRequest(clientIp, "POST", "/crossDrop");
            
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                handlerLogger.warn("[crossDrop] 不支持的HTTP方法: {}", exchange.getRequestMethod());
                logResponse(clientIp, "/crossDrop", 405, "只支持POST");
                HttpUtil.writeXml(exchange, 405, errorXml("只支持POST"));
                return;
            }

            String xml = HttpUtil.readUtf8(exchange);
            handlerLogger.debug("[crossDrop] 收到请求XML: {}", xml.length() > 500 ? xml.substring(0, 500) + "..." : xml);
            
            try {
                Document doc = readXml(xml);
                Element choiceEl = (Element) doc.selectSingleNode("//*[local-name()='choice']");
                if (choiceEl == null) {
                    throw new IllegalArgumentException("缺少choice元素");
                }

                String sno = text(choiceEl, "学号", "Sno", "sid", "id");
                String cno = text(choiceEl, "课程编号", "课程号", "Cno", "cid");
                
                handlerLogger.debug("[crossDrop] 解析到学号: {}, 课程号: {}", sno, cno);
                
                if (isBlank(sno) || isBlank(cno)) {
                    throw new IllegalArgumentException("choice缺少学号/课程编号");
                }

                choiceDao.deleteChoice(sno, cno);
                handlerLogger.info("[crossDrop] 退课成功: 学生 {} 退选课程 {}", sno, cno);
                
                logResponse(clientIp, "/crossDrop", 200, "退课成功");
                HttpUtil.writeXml(exchange, 200, okXml("退课成功"));
            } catch (Exception e) {
                handlerLogger.error("[crossDrop] 处理失败: {}", e.getMessage());
                logError(clientIp, "/crossDrop", e);
                HttpUtil.writeXml(exchange, 400, failXml(e.getMessage()));
            }
        }
    }

    private static Document readXml(String xml) throws DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(new StringReader(xml));
    }

    private static String text(Element parent, String... names) {
        for (String n : names) {
            Element el = (Element) parent.selectSingleNode("./*[local-name()='" + n + "']");
            if (el != null) {
                String t = el.getTextTrim();
                if (!isBlank(t)) {
                    return t;
                }
            }
        }
        return null;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String okXml(String msg) {
        return "<crossSelectResponse><success>true</success><message>" + escapeXml(msg) + "</message></crossSelectResponse>";
    }

    private static String failXml(String msg) {
        return "<crossSelectResponse><success>false</success><message>" + escapeXml(msg) + "</message></crossSelectResponse>";
    }

    private static String errorXml(String msg) {
        return "<error><message>" + escapeXml(msg) + "</message></error>";
    }

    private static String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}