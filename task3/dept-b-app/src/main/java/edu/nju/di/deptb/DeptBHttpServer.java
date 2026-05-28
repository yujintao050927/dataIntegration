package edu.nju.di.deptb;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import edu.nju.di.deptb.dao.ChoiceDao;
import edu.nju.di.deptb.dao.CourseDao;
import edu.nju.di.deptb.dao.StudentDao;
import edu.nju.di.deptb.model.Student;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.concurrent.Executors;

public final class DeptBHttpServer {
    private final HttpServer server;

    public DeptBHttpServer(int port,
                           DeptBXmlService xmlService,
                           StudentDao studentDao,
                           CourseDao courseDao,
                           ChoiceDao choiceDao) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server.setExecutor(Executors.newFixedThreadPool(8));

        server.createContext("/courses", exchange -> {
            try {
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    HttpUtil.writeXml(exchange, 405, errorXml("只支持GET"));
                    return;
                }
                HttpUtil.writeXml(exchange, 200, xmlService.exportCoursesXml());
            } catch (Exception e) {
                HttpUtil.writeXml(exchange, 500, errorXml(e.getMessage()));
            }
        });

        server.createContext("/students", exchange -> {
            try {
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    HttpUtil.writeXml(exchange, 405, errorXml("只支持GET"));
                    return;
                }
                HttpUtil.writeXml(exchange, 200, xmlService.exportStudentsXml());
            } catch (Exception e) {
                HttpUtil.writeXml(exchange, 500, errorXml(e.getMessage()));
            }
        });

        server.createContext("/choices", exchange -> {
            try {
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    HttpUtil.writeXml(exchange, 405, errorXml("只支持GET"));
                    return;
                }
                HttpUtil.writeXml(exchange, 200, xmlService.exportChoicesXml());
            } catch (Exception e) {
                HttpUtil.writeXml(exchange, 500, errorXml(e.getMessage()));
            }
        });

        server.createContext("/crossSelect", new CrossSelectReceiver(studentDao, courseDao, choiceDao));
    }

    public void start() {
        server.start();
    }

    private static final class CrossSelectReceiver implements HttpHandler {
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
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                HttpUtil.writeXml(exchange, 405, errorXml("只支持POST"));
                return;
            }

            String xml = HttpUtil.readUtf8(exchange);
            try {
                Document doc = readXml(xml);
                Element choiceEl = (Element) doc.selectSingleNode("//*[local-name()='choice']");
                if (choiceEl == null) {
                    throw new IllegalArgumentException("缺少choice元素");
                }

                String sno = text(choiceEl, "学号", "Sno", "sid", "id");
                String cno = text(choiceEl, "课程编号", "课程号", "Cno", "cid");
                if (isBlank(sno) || isBlank(cno)) {
                    throw new IllegalArgumentException("choice缺少学号/课程编号");
                }

                // 如果学生信息缺失但请求里带了student，则补全学生
                if (studentDao.findById(sno) == null) {
                    Element studentEl = (Element) doc.selectSingleNode("//*[local-name()='student']");
                    if (studentEl != null) {
                        String name = text(studentEl, "姓名", "Snm", "name");
                        String sex = text(studentEl, "性别", "Sex", "sex");
                        String major = text(studentEl, "专业", "院系", "Sde", "major");
                        if (isBlank(name)) name = sno;
                        if (isBlank(sex)) sex = "男";
                        if (isBlank(major)) major = "计算机科学";
                        studentDao.insertIfAbsent(new Student(sno, name, sex, major, "院系B"));
                    }
                }

                if (courseDao.findById(cno) == null) {
                    throw new IllegalArgumentException("课程不存在: " + cno);
                }

                try {
                    choiceDao.addChoice(sno, cno);
                } catch (Exception e) {
                    // 常见：重复选课触发唯一约束
                    if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                        throw new IllegalArgumentException("重复选课: " + sno + "," + cno);
                    }
                    throw e;
                }

                HttpUtil.writeXml(exchange, 200, okXml("选课成功"));
            } catch (Exception e) {
                HttpUtil.writeXml(exchange, 400, failXml(e.getMessage()));
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
