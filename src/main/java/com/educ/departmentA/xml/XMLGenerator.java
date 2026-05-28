package com.educ.departmentA.xml;

import com.educ.departmentA.model.Course;
import com.educ.departmentA.model.Student;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.StringWriter;
import java.util.List;

public class XMLGenerator {

    public static String generateCoursesXML(List<Course> courses, String department) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("courses");
        root.addAttribute("department", department);

        for (Course course : courses) {
            Element courseElement = root.addElement("course");
            courseElement.addElement("course_id").addText(course.getCourseId());
            courseElement.addElement("course_name").addText(course.getCourseName());
            courseElement.addElement("credit").addText(course.getCredit());
            courseElement.addElement("teacher").addText(course.getTeacher());
            courseElement.addElement("location").addText(course.getLocation());
            courseElement.addElement("is_shared").addText(course.getIsShared());
            if (course.getDepartment() != null) {
                courseElement.addElement("department").addText(course.getDepartment());
            }
        }

        return documentToString(document);
    }

    public static String generateStudentXML(Student student) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("student");

        root.addElement("student_id").addText(student.getStudentId());
        root.addElement("student_name").addText(student.getStudentName());
        root.addElement("gender").addText(student.getGender());
        root.addElement("department").addText(student.getDepartment());
        root.addElement("account_name").addText(student.getAccountName());

        return documentToString(document);
    }

    public static String generateRequestXML(String type, String content) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("request");
        root.addAttribute("type", type);
        root.addText(content);

        return documentToString(document);
    }

    private static String documentToString(Document document) {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            StringWriter writer = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(writer, format);
            xmlWriter.write(document);
            xmlWriter.close();
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
