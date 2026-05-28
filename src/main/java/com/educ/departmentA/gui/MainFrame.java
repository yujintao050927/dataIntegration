package com.educ.departmentA.gui;

import com.educ.departmentA.dao.ChoiceCourseDAO;
import com.educ.departmentA.dao.CourseDAO;
import com.educ.departmentA.http.HttpClient;
import com.educ.departmentA.model.ChoiceCourse;
import com.educ.departmentA.model.Course;
import com.educ.departmentA.model.Student;
import com.educ.departmentA.xml.XMLParser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainFrame extends JFrame {
    private boolean isAdmin;
    private Student currentStudent;
    private CourseDAO courseDAO;
    private ChoiceCourseDAO choiceCourseDAO;
    private JTabbedPane tabbedPane;

    public MainFrame(boolean isAdmin, Student student) {
        this.isAdmin = isAdmin;
        this.currentStudent = student;
        this.courseDAO = new CourseDAO();
        this.choiceCourseDAO = new ChoiceCourseDAO();
        initUI();
    }

    private void initUI() {
        setTitle("院系A - 教务管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // 菜单栏
        JMenuBar menuBar = new JMenuBar();
        JMenu systemMenu = new JMenu("系统");
        JMenuItem logoutItem = new JMenuItem("退出登录");
        JMenuItem exitItem = new JMenuItem("退出系统");

        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        systemMenu.add(logoutItem);
        systemMenu.addSeparator();
        systemMenu.add(exitItem);
        menuBar.add(systemMenu);
        setJMenuBar(menuBar);

        // 顶部信息栏
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        if (isAdmin) {
            infoPanel.add(new JLabel("当前用户: 管理员"));
        } else if (currentStudent != null) {
            infoPanel.add(new JLabel("当前用户: " + currentStudent.getStudentName() + 
                " (" + currentStudent.getStudentId() + ") - " + currentStudent.getDepartment()));
        }
        add(infoPanel, BorderLayout.NORTH);

        // 标签页
        tabbedPane = new JTabbedPane();

        if (!isAdmin && currentStudent != null) {
            tabbedPane.addTab("本院课程", createCoursePanel());
            tabbedPane.addTab("我的选课", createMyCoursePanel());
            tabbedPane.addTab("共享课程", createSharedCoursePanel());
        } else {
            tabbedPane.addTab("课程管理", createCoursePanel());
            // 管理员可以添加更多功能
        }

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 表格
        String[] columnNames = {"课程编号", "课程名称", "学分", "教师", "地点", "是否共享"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 加载数据
        loadCourses(model);

        // 如果是学生，添加选课按钮
        if (!isAdmin) {
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton selectButton = new JButton("选课");

            selectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) {
                        String courseId = (String) model.getValueAt(selectedRow, 0);
                        selectCourse(courseId);
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.this, "请先选择一门课程！", "提示", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });

            buttonPanel.add(selectButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
        }

        return panel;
    }

    private JPanel createMyCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 表格
        String[] columnNames = {"课程编号", "课程名称", "学分", "教师", "成绩"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 加载数据
        loadMyCourses(model);

        // 退课按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton dropButton = new JButton("退课");

        dropButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String courseId = (String) model.getValueAt(selectedRow, 0);
                    dropCourse(courseId);
                    loadMyCourses(model);
                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, "请先选择一门课程！", "提示", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        buttonPanel.add(dropButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSharedCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 表格
        String[] columnNames = {"所属院系", "课程编号", "课程名称", "学分", "教师", "地点"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton refreshButton = new JButton("刷新");
        JButton crossSelectButton = new JButton("跨院选课");

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSharedCourses(model);
            }
        });

        crossSelectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String department = (String) model.getValueAt(selectedRow, 0);
                    String courseId = (String) model.getValueAt(selectedRow, 1);
                    crossSelectCourse(courseId, department);
                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, "请先选择一门课程！", "提示", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        buttonPanel.add(refreshButton);
        buttonPanel.add(crossSelectButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 初始加载
        loadSharedCourses(model);

        return panel;
    }

    private void loadCourses(DefaultTableModel model) {
        model.setRowCount(0);
        List<Course> courses = courseDAO.getAllCourses();
        for (Course course : courses) {
            Object[] row = {
                course.getCourseId(),
                course.getCourseName(),
                course.getCredit(),
                course.getTeacher(),
                course.getLocation(),
                "Y".equals(course.getIsShared()) ? "是" : "否"
            };
            model.addRow(row);
        }
    }

    private void loadMyCourses(DefaultTableModel model) {
        model.setRowCount(0);
        List<ChoiceCourse> choices = choiceCourseDAO.getCoursesByStudentId(currentStudent.getStudentId());
        for (ChoiceCourse choice : choices) {
            Course course = courseDAO.getCourseById(choice.getCourseId());
            if (course != null) {
                Object[] row = {
                    course.getCourseId(),
                    course.getCourseName(),
                    course.getCredit(),
                    course.getTeacher(),
                    choice.getScore() != null ? choice.getScore() : "未出"
                };
                model.addRow(row);
            }
        }
    }

    private void loadSharedCourses(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            String response = HttpClient.getSharedCourses();
            if (!response.isEmpty()) {
                List<Course> courses = XMLParser.parseCoursesXML(response);
                for (Course course : courses) {
                    Object[] row = {
                        course.getDepartment(),
                        course.getCourseId(),
                        course.getCourseName(),
                        course.getCredit(),
                        course.getTeacher(),
                        course.getLocation()
                    };
                    model.addRow(row);
                }
            } else {
                JOptionPane.showMessageDialog(this, "无法获取共享课程，请检查集成服务器是否启动！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "获取共享课程失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectCourse(String courseId) {
        if (choiceCourseDAO.hasChoiced(courseId, currentStudent.getStudentId())) {
            JOptionPane.showMessageDialog(this, "您已经选择了这门课程！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (choiceCourseDAO.addChoiceCourse(courseId, currentStudent.getStudentId())) {
            JOptionPane.showMessageDialog(this, "选课成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "选课失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dropCourse(String courseId) {
        int confirm = JOptionPane.showConfirmDialog(this, "确定要退选这门课程吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (choiceCourseDAO.dropChoiceCourse(courseId, currentStudent.getStudentId())) {
                JOptionPane.showMessageDialog(this, "退课成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "退课失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void crossSelectCourse(String courseId, String department) {
        int confirm = JOptionPane.showConfirmDialog(this, "确定要选择这门跨院课程吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String response = HttpClient.crossSelectCourse(currentStudent.getStudentId(), courseId, department);
                String result = XMLParser.parseResponseXML(response);
                if ("success".equals(result)) {
                    JOptionPane.showMessageDialog(this, "跨院选课成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "跨院选课失败：" + result, "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "跨院选课失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        this.dispose();
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);
    }
}
