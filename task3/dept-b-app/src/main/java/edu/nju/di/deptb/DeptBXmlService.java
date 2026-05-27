package edu.nju.di.deptb;

import edu.nju.di.deptb.dao.ChoiceDao;
import edu.nju.di.deptb.dao.CourseDao;
import edu.nju.di.deptb.dao.StudentDao;
import edu.nju.di.deptb.model.Choice;
import edu.nju.di.deptb.model.Course;
import edu.nju.di.deptb.model.Student;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class DeptBXmlService {
    private final StudentDao studentDao;
    private final CourseDao courseDao;
    private final ChoiceDao choiceDao;

    public DeptBXmlService(StudentDao studentDao, CourseDao courseDao, ChoiceDao choiceDao) {
        this.studentDao = studentDao;
        this.courseDao = courseDao;
        this.choiceDao = choiceDao;
    }

    // B院系课程XML：用于formatClass.xsl输入
    public String exportCoursesXml() throws Exception {
        List<Course> courses = courseDao.listAll();
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("classes");
        for (Course c : courses) {
            Element cls = root.addElement("class");
            cls.addElement("课程号").setText(nz(c.cno()));
            cls.addElement("课程名").setText(nz(c.name()));
            cls.addElement("学时").setText(nz(c.hours()));
            cls.addElement("学分").setText(nz(c.credit()));
            cls.addElement("教师").setText(nz(c.teacher()));
            cls.addElement("地点").setText(nz(c.location()));
            cls.addElement("属性").setText(nz(c.attr()));
        }
        return toXml(doc);
    }

    // B院系学生XML：用于formatStudent.xsl输入
    public String exportStudentsXml() throws Exception {
        List<Student> students = studentDao.listAll();
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("students");
        for (Student s : students) {
            Element stu = root.addElement("student");
            stu.addElement("学号").setText(nz(s.sno()));
            stu.addElement("姓名").setText(nz(s.name()));
            stu.addElement("性别").setText(nz(s.sex()));
            stu.addElement("专业").setText(nz(s.major()));
            stu.addElement("院系").setText(nz(s.dept()));
        }
        return toXml(doc);
    }

    // B院系选课XML：用于formatClassChoice.xsl输入（表3-19：cid对应“课程编号”，score对应“得分”）
    public String exportChoicesXml() throws Exception {
        List<Choice> choices = choiceDao.listAll();
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("choices");
        for (Choice c : choices) {
            Element ch = root.addElement("choice");
            ch.addElement("学号").setText(nz(c.sno()));
            ch.addElement("课程编号").setText(nz(c.cno()));
            ch.addElement("得分").setText(nz(c.score()));
        }
        return toXml(doc);
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }

    private static String toXml(Document doc) throws Exception {
        OutputFormat fmt = OutputFormat.createPrettyPrint();
        fmt.setEncoding(StandardCharsets.UTF_8.name());
        StringWriter sw = new StringWriter();
        XMLWriter writer = new XMLWriter(sw, fmt);
        writer.write(doc);
        writer.flush();
        return sw.toString();
    }
}
