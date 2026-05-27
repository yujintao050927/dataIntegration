package com.educ.departmentA.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClient {

    private static final String INTEGRATION_SERVER_URL = "http://localhost:8090";

    public static String sendRequest(String endpoint, String xmlData) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(INTEGRATION_SERVER_URL + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/xml; charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // 发送请求数据
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = xmlData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 读取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return response.toString();
                }
            } else {
                System.err.println("HTTP Error Code: " + responseCode);
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String getSharedCourses() {
        String requestXML = "<request type=\"share\">A</request>";
        return sendRequest("/share", requestXML);
    }

    public static String crossSelectCourse(String studentId, String courseId, String department) {
        String requestXML = String.format(
            "<request type=\"cross_select\">" +
            "<student_id>%s</student_id>" +
            "<course_id>%s</course_id>" +
            "<department>%s</department>" +
            "</request>",
            studentId, courseId, department
        );
        return sendRequest("/cross_select", requestXML);
    }

    public static String crossDropCourse(String studentId, String courseId, String department) {
        String requestXML = String.format(
            "<request type=\"cross_drop\">" +
            "<student_id>%s</student_id>" +
            "<course_id>%s</course_id>" +
            "<department>%s</department>" +
            "</request>",
            studentId, courseId, department
        );
        return sendRequest("/cross_drop", requestXML);
    }
}
