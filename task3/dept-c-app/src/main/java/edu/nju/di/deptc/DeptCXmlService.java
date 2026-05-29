package edu.nju.di.deptc;

import edu.nju.di.deptc.dao.ChoiceDao;
import edu.nju.di.deptc.dao.CourseDao;
import edu.nju.di.deptc.dao.StudentDao;
import edu.nju.di.deptc.model.Choice;
import edu.nju.di.deptc.model.Course;
import edu.nju.di.deptc.model.Student;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class DeptCXmlService {
    private final StudentDao studentDao;
    private final CourseDao courseDao;
    private final ChoiceDao choiceDao;

    public DeptCXmlService(StudentDao studentDao, CourseDao courseDao, ChoiceDao choiceDao) {
        this.studentDao = studentDao;
        this.courseDao = courseDao;
        this.choiceDao = choiceDao;
    }

    // C院系课程XML：用于formatClass.xsl输入
    public String exportCoursesXml() throws Exception {
        List<Course> courses = courseDao.listAll();
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("classes");
        for (Course c : courses) {
            Element cls = root.addElement("class");
            cls.addElement("Cno").setText(nz(c.cno()));
            cls.addElement("Cnm").setText(nz(c.cnm()));
            cls.addElement("Cpt").setText(String.valueOf(c.cpt()));
            cls.addElement("Tec").setText(nz(c.tec()));
            cls.addElement("Pla").setText(nz(c.pla()));
            cls.addElement("share_flag").setText(c.shareFlag() != null ? String.valueOf(c.shareFlag()) : "");
        }
        return toXml(doc);
    }

    // C院系学生XML：用于formatStudent.xsl输入
    public String exportStudentsXml() throws Exception {
        List<Student> students = studentDao.listAll();
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("students");
        for (Student s : students) {
            Element stu = root.addElement("student");
            stu.addElement("Sno").setText(nz(s.sno()));
            stu.addElement("Snm").setText(nz(s.snm()));
            stu.addElement("Sex").setText(nz(s.sex()));
            stu.addElement("Sde").setText(nz(s.sde()));
        }
        return toXml(doc);
    }

    // C院系选课XML：用于formatClassChoice.xsl输入
    public String exportChoicesXml() throws Exception {
        List<Choice> choices = choiceDao.listAll();
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("choices");
        for (Choice c : choices) {
            Element ch = root.addElement("choice");
            ch.addElement("Sno").setText(nz(c.sno()));
            ch.addElement("Cno").setText(nz(c.cno()));
            ch.addElement("Grd").setText(c.grd() == null ? "" : String.valueOf(c.grd()));
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
