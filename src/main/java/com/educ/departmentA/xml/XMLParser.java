package com.educ.departmentA.xml;

import com.educ.departmentA.model.Course;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class XMLParser {

    public static List<Course> parseCoursesXML(String xmlString) {
        List<Course> courses = new ArrayList<>();
        
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new StringReader(xmlString));
            Element root = document.getRootElement();
            
            List<Element> courseElements = root.elements("course");
            
            for (Element courseElement : courseElements) {
                Course course = new Course();
                course.setCourseId(getElementText(courseElement, "course_id"));
                course.setCourseName(getElementText(courseElement, "course_name"));
                course.setCredit(getElementText(courseElement, "credit"));
                course.setTeacher(getElementText(courseElement, "teacher"));
                course.setLocation(getElementText(courseElement, "location"));
                course.setIsShared(getElementText(courseElement, "is_shared"));
                course.setDepartment(getElementText(courseElement, "department"));
                courses.add(course);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        
        return courses;
    }

    public static String parseResponseXML(String xmlString) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new StringReader(xmlString));
            Element root = document.getRootElement();
            return root.getTextTrim();
        } catch (DocumentException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getElementText(Element parent, String elementName) {
        Element element = parent.element(elementName);
        return element != null ? element.getTextTrim() : "";
    }
}
