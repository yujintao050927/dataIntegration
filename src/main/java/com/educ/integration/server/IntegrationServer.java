package com.educ.integration.server;

import com.educ.integration.validation.XMLValidator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class IntegrationServer {
    private static final int PORT = 8090;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        server.createContext("/share", new ShareHandler());
        server.createContext("/cross_select", new CrossSelectHandler());
        server.createContext("/cross_drop", new CrossDropHandler());
        server.createContext("/statistics", new StatisticsHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("集成服务器已启动，监听端口: " + PORT);
        System.out.println("可用接口:");
        System.out.println("  - /share: 课程共享");
        System.out.println("  - /cross_select: 跨院选课");
        System.out.println("  - /cross_drop: 跨院退课");
        System.out.println("  - /statistics: 统计信息");
    }

    static class ShareHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String requestBody = readRequestBody(exchange);
                
                if (XMLValidator.validateRequest(requestBody)) {
                    // 解析请求，获取请求的院系
                    String requestingDept = extractDepartment(requestBody);
                    
                    // 从B、C获取共享课程
                    String coursesFromB = getCoursesFromDepartment("B");
                    String coursesFromC = getCoursesFromDepartment("C");
                    
                    // 合并并转换格式
                    String mergedCourses = mergeAndTransform(coursesFromB, coursesFromC, requestingDept);
                    
                    sendResponse(exchange, 200, mergedCourses);
                } else {
                    sendResponse(exchange, 400, "<response>invalid_xml_format</response>");
                }
            } else {
                sendResponse(exchange, 405, "<response>method_not_allowed</response>");
            }
        }
    }

    static class CrossSelectHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String requestBody = readRequestBody(exchange);
                
                if (XMLValidator.validateRequest(requestBody)) {
                    // 处理跨院选课逻辑
                    String result = processCrossSelect(requestBody);
                    sendResponse(exchange, 200, "<response>" + result + "</response>");
                } else {
                    sendResponse(exchange, 400, "<response>invalid_xml_format</response>");
                }
            } else {
                sendResponse(exchange, 405, "<response>method_not_allowed</response>");
            }
        }
    }

    static class CrossDropHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String requestBody = readRequestBody(exchange);
                
                if (XMLValidator.validateRequest(requestBody)) {
                    // 处理跨院退课逻辑
                    String result = processCrossDrop(requestBody);
                    sendResponse(exchange, 200, "<response>" + result + "</response>");
                } else {
                    sendResponse(exchange, 400, "<response>invalid_xml_format</response>");
                }
            } else {
                sendResponse(exchange, 405, "<response>method_not_allowed</response>");
            }
        }
    }

    static class StatisticsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod()) || "POST".equals(exchange.getRequestMethod())) {
                String statistics = generateStatistics();
                sendResponse(exchange, 200, statistics);
            } else {
                sendResponse(exchange, 405, "<response>method_not_allowed</response>");
            }
        }
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/xml; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
    }

    private static String extractDepartment(String requestBody) {
        // 简单解析，实际可以用DOM4J
        if (requestBody.contains(">A<")) return "A";
        if (requestBody.contains(">B<")) return "B";
        if (requestBody.contains(">C<")) return "C";
        return "A";
    }

    private static String getCoursesFromDepartment(String dept) {
        // 模拟从其他院系获取课程
        // 实际应该通过HTTP调用对应院系的接口
        return generateMockCourses(dept);
    }

    private static String generateMockCourses(String dept) {
        return "<courses department=\"" + dept + "\">" +
                "<course>" +
                "<course_id>" + dept + "001</course_id>" +
                "<course_name>共享课程" + dept + "1</course_name>" +
                "<credit>4</credit>" +
                "<teacher>张老师</teacher>" +
                "<location>教学楼</location>" +
                "<is_shared>Y</is_shared>" +
                "</course>" +
                "<course>" +
                "<course_id>" + dept + "002</course_id>" +
                "<course_name>共享课程" + dept + "2</course_name>" +
                "<credit>3</credit>" +
                "<teacher>李老师</teacher>" +
                "<location>实验楼</location>" +
                "<is_shared>Y</is_shared>" +
                "</course>" +
                "</courses>";
    }

    private static String mergeAndTransform(String coursesB, String coursesC, String targetDept) {
        // 简单合并，实际应该用XSLT转换为统一格式再合并
        return "<courses department=\"merged\">" +
                extractCourseElements(coursesB, "B") +
                extractCourseElements(coursesC, "C") +
                "</courses>";
    }

    private static String extractCourseElements(String xml, String dept) {
        // 简单的XML处理，实际应该用DOM4J
        StringBuilder sb = new StringBuilder();
        String[] lines = xml.split("\n");
        boolean inCourse = false;
        
        for (String line : lines) {
            if (line.contains("<course>")) {
                inCourse = true;
                sb.append(line).append("\n");
            } else if (line.contains("</course>")) {
                inCourse = false;
                sb.append("  <department>").append(dept).append("</department>\n");
                sb.append(line).append("\n");
            } else if (inCourse) {
                sb.append(line).append("\n");
            }
        }
        
        return sb.toString();
    }

    private static String processCrossSelect(String requestBody) {
        // 模拟跨院选课处理
        System.out.println("处理跨院选课请求: " + requestBody);
        return "success";
    }

    private static String processCrossDrop(String requestBody) {
        // 模拟跨院退课处理
        System.out.println("处理跨院退课请求: " + requestBody);
        return "success";
    }

    private static String generateStatistics() {
        return "<statistics>" +
                "<total_students>150</total_students>" +
                "<total_courses>30</total_courses>" +
                "<total_selections>750</total_selections>" +
                "</statistics>";
    }
}
