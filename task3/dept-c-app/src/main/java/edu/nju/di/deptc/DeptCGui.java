package edu.nju.di.deptc;

import edu.nju.di.deptc.dao.AccountDao;
import edu.nju.di.deptc.dao.ChoiceDao;
import edu.nju.di.deptc.dao.CourseDao;
import edu.nju.di.deptc.dao.StudentDao;
import edu.nju.di.deptc.model.Choice;
import edu.nju.di.deptc.model.Course;
import edu.nju.di.deptc.model.Student;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DeptCGui {

    private final AccountDao accountDao;
    private final StudentDao studentDao;
    private final CourseDao courseDao;
    private final ChoiceDao choiceDao;

    private String integrationShareUrl = "http://localhost:8090/share";
    private String integrationCrossSelectUrl = "http://localhost:8090/crossSelect";
    private String integrationCrossDropUrl = "http://localhost:8090/crossDrop";
    private String integrationStatisticsUrl = "http://localhost:8090/statistics";

    private final Map<String, String> choiceSourceCache = new ConcurrentHashMap<>();

    private static final Color PRIMARY = new Color(59, 130, 246);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color WARNING = new Color(245, 158, 11);
    private static final Color SECONDARY = new Color(107, 114, 128);

    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color BORDER = new Color(220, 225, 232);

    public DeptCGui(AccountDao accountDao,
                    StudentDao studentDao,
                    CourseDao courseDao,
                    ChoiceDao choiceDao) {

        this.accountDao = accountDao;
        this.studentDao = studentDao;
        this.courseDao = courseDao;
        this.choiceDao = choiceDao;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        UIManager.put("TabbedPane.selected", new Color(230, 240, 255));
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        UIManager.put("TabbedPane.focus", new Color(0, 0, 0, 0));
    }

    public void showLogin() {

        JFrame f = new JFrame("院系C - 学生选课系统");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setSize(750, 520);
        f.setResizable(false);
        f.setLocationRelativeTo(null);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(245, 247, 250));

        JPanel card = new JPanel(new BorderLayout(20, 20));
        card.setPreferredSize(new Dimension(600, 380));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 215, 240), 1),
                new EmptyBorder(35, 40, 35, 40)
        ));

        JLabel title = new JLabel("院系C - 学生选课系统", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 28));
        title.setForeground(new Color(30, 64, 175));

        JLabel subTitle = new JLabel("Department C Course Selection System", SwingConstants.CENTER);
        subTitle.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        subTitle.setForeground(new Color(120, 120, 120));

        JPanel top = new JPanel(new GridLayout(2, 1, 0, 5));
        top.setOpaque(false);
        top.add(title);
        top.add(subTitle);

        card.add(top, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;

        JLabel accLabel = new JLabel("账号");
        accLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
        accLabel.setForeground(new Color(60, 60, 60));
        
        JLabel pwdLabel = new JLabel("密码");
        pwdLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
        pwdLabel.setForeground(new Color(60, 60, 60));

        JTextField acc = new JTextField(30);
        acc.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        acc.setPreferredSize(new Dimension(380, 45));
        acc.setMinimumSize(new Dimension(380, 45));
        acc.setMaximumSize(new Dimension(380, 45));
        acc.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JPasswordField pwd = new JPasswordField(30);
        pwd.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        pwd.setPreferredSize(new Dimension(380, 45));
        pwd.setMinimumSize(new Dimension(380, 45));
        pwd.setMaximumSize(new Dimension(380, 45));
        pwd.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(accLabel, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1;
        gbc.gridx = 1;
        form.add(acc, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(pwdLabel, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1;
        gbc.gridx = 1;
        form.add(pwd, gbc);

        card.add(form, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        JButton login = new JButton("登 录");
        login.setPreferredSize(new Dimension(200, 48));
        login.setFont(new Font("微软雅黑", Font.BOLD, 16));
        login.setBackground(new Color(59, 130, 246));
        login.setForeground(Color.WHITE);
        login.setFocusPainted(false);
        login.setBorderPainted(false);
        login.setCursor(new Cursor(Cursor.HAND_CURSOR));

        login.addActionListener(e -> {
            try {

                String a = acc.getText().trim();
                String p = new String(pwd.getPassword());

                if (a.isEmpty() || p.isEmpty()) {
                    JOptionPane.showMessageDialog(f,
                            "账号和密码不能为空",
                            "提示",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                AccountDao.LoginResult r = accountDao.login(a, p);

                if (r == null) {
                    JOptionPane.showMessageDialog(f,
                            "账号或密码错误",
                            "登录失败",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                f.dispose();

                if (r.isAdmin()) {
                    showAdmin();
                } else {

                    if (r.sno() == null || r.sno().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null,
                                "学生账号缺少学号绑定");
                        showLogin();
                        return;
                    }

                    showStudent(r.sno());
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f,
                        "登录失败: " + ex.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(login);
        card.add(buttonPanel, BorderLayout.SOUTH);

        root.add(card);

        f.setContentPane(root);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private void showStudent(String sno) {

        JFrame f = new JFrame("院系C - 学生选课系统");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setSize(1200, 760);
        f.setMinimumSize(new Dimension(1000, 650));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("微软雅黑", Font.BOLD, 14));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("我的选课", createMyCoursesPanel(f, sno));
        tabs.addTab("本院选课", createSelectCoursesPanel(f, sno));
        tabs.addTab("跨系选课", createSharePanel(f, sno));

        root.add(tabs, BorderLayout.CENTER);

        f.setContentPane(root);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private JPanel createMyCoursesPanel(JFrame parent, String sno) {

        JPanel panel = createPagePanel("我的选课");

        DefaultTableModel choiceModel = new DefaultTableModel(
                new String[]{"课程编号", "课程名称", "课程来源", "成绩"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = createBeautifulTable(choiceModel);

        TableColumn col0 = table.getColumnModel().getColumn(0);
        col0.setPreferredWidth(120);

        TableColumn col1 = table.getColumnModel().getColumn(1);
        col1.setPreferredWidth(300);

        JScrollPane scrollPane = createScrollPane(table);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bottom.setOpaque(false);

        JButton refresh = createButton("刷 新", SECONDARY, 130, 42);
        JButton drop = createButton("退 课", DANGER, 130, 42);

        Runnable reload = () -> {

            choiceModel.setRowCount(0);
            choiceSourceCache.clear();

            try {

                List<Choice> choices = choiceDao.listByStudent(sno);

                for (Choice c : choices) {

                    Course co = courseDao.findById(c.cno());

                    String sourceDept = getCourseSource(c.cno());

                    choiceSourceCache.put(c.cno(), sourceDept);

                    choiceModel.addRow(new Object[]{
                            c.cno(),
                            co != null ? co.cnm() : "未知课程",
                            sourceDept.isEmpty() ? "未知" : sourceDept,
                            c.grd() != null ? c.grd() : "-"
                    });
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent,
                        "加载选课失败: " + ex.getMessage());
            }
        };

        refresh.addActionListener(e -> reload.run());

        drop.addActionListener(e -> {

            int row = table.getSelectedRow();

            if (row < 0) {
                JOptionPane.showMessageDialog(parent,
                        "请选择要退课的课程");
                return;
            }

            String cno = String.valueOf(choiceModel.getValueAt(row, 0));
            String sourceDept = String.valueOf(choiceModel.getValueAt(row, 2));

            try {

                if ("C".equals(sourceDept)) {
                    choiceDao.deleteChoice(sno, cno);
                    JOptionPane.showMessageDialog(parent,
                            "退课成功");
                } else {

                    String result = doCrossDrop(sno, cno, sourceDept);

                    JOptionPane.showMessageDialog(parent,
                            result);
                }

                reload.run();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent,
                        "退课失败: " + ex.getMessage());
            }
        });

        bottom.add(refresh);
        bottom.add(drop);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        reload.run();

        return panel;
    }

    private JPanel createSelectCoursesPanel(JFrame parent, String sno) {

        JPanel panel = createPagePanel("本院课程");

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"课程编号", "课程名称", "学分", "教师", "容量"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = createBeautifulTable(model);

        JScrollPane scrollPane = createScrollPane(table);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bottom.setOpaque(false);

        JButton refresh = createButton("刷 新", SECONDARY, 130, 42);
        JButton select = createButton("选 课", SUCCESS, 130, 42);

        Runnable reload = () -> {

            model.setRowCount(0);

            try {

                List<Course> courses = courseDao.listAll();

                for (Course c : courses) {

                    model.addRow(new Object[]{
                            c.cno(),
                            c.cnm(),
                            c.credit(),
                            c.tea(),
                            c.capacity()
                    });
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent,
                        "加载课程失败: " + ex.getMessage());
            }
        };

        refresh.addActionListener(e -> reload.run());

        select.addActionListener(e -> {

            int row = table.getSelectedRow();

            if (row < 0) {
                JOptionPane.showMessageDialog(parent,
                        "请选择课程");
                return;
            }

            String cno = String.valueOf(model.getValueAt(row, 0));

            try {

                choiceDao.addChoice(sno, cno);

                JOptionPane.showMessageDialog(parent,
                        "选课成功");

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(parent,
                        "选课失败: " + ex.getMessage());
            }
        });

        bottom.add(refresh);
        bottom.add(select);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        reload.run();

        return panel;
    }

    private JPanel createSharePanel(JFrame parent, String sno) {

        JPanel panel = createPagePanel("跨系共享课程");

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"课程编号", "课程名称", "学分", "教师", "地点", "来源院系"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = createBeautifulTable(model);

        JScrollPane scrollPane = createScrollPane(table);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bottom.setOpaque(false);

        JButton fetch = createButton("获取共享课程", SECONDARY, 180, 42);
        JButton select = createButton("跨系选课", PRIMARY, 150, 42);

        fetch.addActionListener(e -> {

            model.setRowCount(0);

            try {

                String xml = fetchSharedCourses();

                List<SharedCourse> courses = parseSharedCourses(xml);

                for (SharedCourse c : courses) {

                    model.addRow(new Object[]{
                            c.id,
                            c.name,
                            c.score,
                            c.teacher,
                            c.location,
                            c.source
                    });
                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(parent,
                        "获取共享课程失败: " + ex.getMessage());
            }
        });

        select.addActionListener(e -> {

            int row = table.getSelectedRow();

            if (row < 0) {
                JOptionPane.showMessageDialog(parent,
                        "请选择课程");
                return;
            }

            String cno = String.valueOf(model.getValueAt(row, 0));
            String sourceDept = String.valueOf(model.getValueAt(row, 5));

            try {

                String result = doCrossSelect(sno, cno, sourceDept);

                JOptionPane.showMessageDialog(parent,
                        result);

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(parent,
                        "跨系选课失败: " + ex.getMessage());
            }
        });

        bottom.add(fetch);
        bottom.add(select);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private void showAdmin() {

        JFrame f = new JFrame("院系C - 管理员系统");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setSize(800, 550);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("管理员控制台", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 30));
        title.setForeground(new Color(30, 64, 175));

        root.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(3, 2, 25, 25));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(30, 0, 0, 0));

        JButton addStudent = createButton("添加学生", SUCCESS, 250, 70);
        JButton addCourse = createButton("添加课程", SUCCESS, 250, 70);

        JButton listStudents = createButton("查看学生", PRIMARY, 250, 70);
        JButton listCourses = createButton("查看课程", PRIMARY, 250, 70);

        JButton listChoices = createButton("查看选课", new Color(139, 92, 246), 250, 70);
        JButton statistics = createButton("集成统计", WARNING, 250, 70);

        addStudent.addActionListener(e -> showAddStudentDialog(f));
        addCourse.addActionListener(e -> showAddCourseDialog(f));

        listStudents.addActionListener(e -> showStudentList(f));
        listCourses.addActionListener(e -> showCourseList(f));
        listChoices.addActionListener(e -> showChoiceList(f));

        statistics.addActionListener(e -> {
            try {

                String result = fetchStatistics();

                JOptionPane.showMessageDialog(f,
                        result,
                        "统计结果",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(f,
                        "获取统计失败: " + ex.getMessage());
            }
        });

        grid.add(addStudent);
        grid.add(addCourse);
        grid.add(listStudents);
        grid.add(listCourses);
        grid.add(listChoices);
        grid.add(statistics);

        root.add(grid, BorderLayout.CENTER);

        f.setContentPane(root);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private JPanel createPagePanel(String titleText) {

        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topCard = new JPanel(new BorderLayout());
        topCard.setBackground(CARD);
        topCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("微软雅黑", Font.BOLD, 22));
        title.setForeground(new Color(30, 64, 175));

        topCard.add(title, BorderLayout.WEST);

        root.add(topCard, BorderLayout.NORTH);

        return root;
    }

    private JTable createBeautifulTable(DefaultTableModel model) {

        JTable table = new JTable(model);

        table.setRowHeight(36);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        table.setGridColor(new Color(235, 235, 235));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();

        header.setFont(new Font("微软雅黑", Font.BOLD, 14));
        header.setBackground(new Color(239, 246, 255));
        header.setForeground(new Color(30, 41, 59));
        header.setPreferredSize(new Dimension(header.getWidth(), 42));

        return table;
    }

    private JScrollPane createScrollPane(JTable table) {

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);

        return scrollPane;
    }

    private JLabel createFieldLabel(String text) {

        JLabel label = new JLabel(text + "：");

        label.setFont(new Font("微软雅黑", Font.BOLD, 16));
        label.setPreferredSize(new Dimension(70, 40));

        return label;
    }

    private JTextField createInputField() {

        JTextField field = new JTextField();

        field.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        field.setPreferredSize(new Dimension(320, 46));

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215)),
                new EmptyBorder(8, 14, 8, 14)
        ));

        return field;
    }

    private JPasswordField createPasswordField() {

        JPasswordField field = new JPasswordField();

        field.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        field.setPreferredSize(new Dimension(320, 46));

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215)),
                new EmptyBorder(8, 14, 8, 14)
        ));

        return field;
    }

    private JButton createButton(String text,
                                 Color bg,
                                 int width,
                                 int height) {

        JButton button = new JButton(text);

        button.setPreferredSize(new Dimension(width, height));
        button.setMinimumSize(new Dimension(width, height));

        button.setBackground(bg);
        button.setForeground(Color.WHITE);

        button.setFont(new Font("微软雅黑", Font.BOLD, 18));

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setOpaque(true);
        
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bg.darker());
                button.setBorder(BorderFactory.createLineBorder(bg.darker().darker(), 2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bg);
                button.setBorder(BorderFactory.createEmptyBorder());
            }
        });
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void showAddStudentDialog(JFrame parent) {

        JPanel form = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);

        JTextField sno = createInputField();
        JTextField snm = createInputField();
        JTextField sex = createInputField();
        JTextField sde = createInputField();
        JTextField pwd = createInputField();

        addFormField(form, gbc, 0, "学号", sno);
        addFormField(form, gbc, 1, "姓名", snm);
        addFormField(form, gbc, 2, "性别", sex);
        addFormField(form, gbc, 3, "院系", sde);
        addFormField(form, gbc, 4, "密码", pwd);

        if (JOptionPane.showConfirmDialog(
                parent,
                form,
                "添加学生",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        ) != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            Student s = new Student(
                    sno.getText().trim(),
                    snm.getText().trim(),
                    sex.getText().trim(),
                    sde.getText().trim(),
                    pwd.getText().trim()
            );

            studentDao.insertIfAbsent(s);

            JOptionPane.showMessageDialog(parent,
                    "添加成功");

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(parent,
                    "添加失败: " + ex.getMessage());
        }
    }

    private void showAddCourseDialog(JFrame parent) {

        JPanel form = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);

        JTextField cno = createInputField();
        JTextField cnm = createInputField();
        JTextField cde = createInputField();
        JTextField credit = createInputField();
        JTextField tea = createInputField();
        JTextField capacity = createInputField();

        addFormField(form, gbc, 0, "课程编号", cno);
        addFormField(form, gbc, 1, "课程名称", cnm);
        addFormField(form, gbc, 2, "院系", cde);
        addFormField(form, gbc, 3, "学分", credit);
        addFormField(form, gbc, 4, "教师", tea);
        addFormField(form, gbc, 5, "容量", capacity);

        if (JOptionPane.showConfirmDialog(
                parent,
                form,
                "添加课程",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        ) != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            Course c = new Course(
                    cno.getText().trim(),
                    cnm.getText().trim(),
                    cde.getText().trim(),
                    Integer.parseInt(credit.getText().trim()),
                    tea.getText().trim(),
                    Integer.parseInt(capacity.getText().trim())
            );

            courseDao.insertIfAbsent(c);

            JOptionPane.showMessageDialog(parent,
                    "添加成功");

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(parent,
                    "添加失败: " + ex.getMessage());
        }
    }

    private void addFormField(JPanel form,
                              GridBagConstraints gbc,
                              int row,
                              String label,
                              JTextField field) {

        JLabel l = new JLabel(label + "：");

        l.setFont(new Font("微软雅黑", Font.BOLD, 15));

        gbc.gridx = 0;
        gbc.gridy = row;

        form.add(l, gbc);

        gbc.gridx = 1;

        form.add(field, gbc);
    }

    private void showStudentList(JFrame parent) {

        StringBuilder sb = new StringBuilder();

        try {

            List<Student> students = studentDao.listAll();

            sb.append("学生列表\n\n");

            for (Student s : students) {

                sb.append("学号: ").append(s.sno())
                        .append("    姓名: ").append(s.snm())
                        .append("    性别: ").append(s.sex())
                        .append("    院系: ").append(s.sde())
                        .append("\n");
            }

        } catch (Exception ex) {

            sb.append("获取失败: ").append(ex.getMessage());
        }

        JTextArea area = new JTextArea(sb.toString());

        area.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        area.setEditable(false);

        JScrollPane pane = new JScrollPane(area);
        pane.setPreferredSize(new Dimension(700, 400));

        JOptionPane.showMessageDialog(parent,
                pane,
                "学生列表",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showCourseList(JFrame parent) {

        StringBuilder sb = new StringBuilder();

        try {

            List<Course> courses = courseDao.listAll();

            sb.append("课程列表\n\n");

            for (Course c : courses) {

                sb.append("课程: ").append(c.cnm())
                        .append("    教师: ").append(c.tea())
                        .append("    学分: ").append(c.credit())
                        .append("\n");
            }

        } catch (Exception ex) {

            sb.append("获取失败: ").append(ex.getMessage());
        }

        JTextArea area = new JTextArea(sb.toString());

        area.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        area.setEditable(false);

        JScrollPane pane = new JScrollPane(area);
        pane.setPreferredSize(new Dimension(700, 400));

        JOptionPane.showMessageDialog(parent,
                pane,
                "课程列表",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showChoiceList(JFrame parent) {

        StringBuilder sb = new StringBuilder();

        try {

            List<Choice> choices = choiceDao.listAll();

            sb.append("选课列表\n\n");

            for (Choice c : choices) {

                sb.append("学号: ").append(c.sno())
                        .append("    课程号: ").append(c.cno())
                        .append("    成绩: ").append(c.grd())
                        .append("\n");
            }

        } catch (Exception ex) {

            sb.append("获取失败: ").append(ex.getMessage());
        }

        JTextArea area = new JTextArea(sb.toString());

        area.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        area.setEditable(false);

        JScrollPane pane = new JScrollPane(area);
        pane.setPreferredSize(new Dimension(700, 400));

        JOptionPane.showMessageDialog(parent,
                pane,
                "选课列表",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private String getCourseSource(String cno) {

        if (choiceSourceCache.containsKey(cno)) {
            return choiceSourceCache.get(cno);
        }

        try {

            Course course = courseDao.findById(cno);

            if (course != null) {
                return "C";
            }

        } catch (Exception ignored) {
        }

        return "";
    }

    private String fetchSharedCourses() throws Exception {

        String requestXml =
                "<shareRequest><requester>C</requester></shareRequest>";

        return sendPost(integrationShareUrl, requestXml);
    }

    private List<SharedCourse> parseSharedCourses(String xml) throws Exception {

        List<SharedCourse> list = new ArrayList<>();

        SAXReader reader = new SAXReader();

        Document doc = reader.read(new StringReader(xml));

        List<?> nodes = doc.selectNodes("//*[local-name()='class']");

        for (Object o : nodes) {

            Element el = (Element) o;

            String id = text(el, "id", "课程号", "Cno");
            String name = text(el, "name", "课程名", "Cnm");
            String score = text(el, "score", "学分", "Cpt");
            String teacher = text(el, "teacher", "教师", "Tec");
            String location = text(el, "location", "地点", "Pla");
            String source = text(el, "sourceDept", "source", "院系");

            list.add(new SharedCourse(
                    id == null ? "" : id,
                    name == null ? "" : name,
                    score == null ? "" : score,
                    teacher == null ? "" : teacher,
                    location == null ? "" : location,
                    source == null ? "" : source
            ));
        }

        return list;
    }

    private String doCrossSelect(String sno,
                                 String cno,
                                 String targetDept) throws Exception {

        Student stu = studentDao.findById(sno);

        if (stu == null) {
            throw new IllegalStateException("学生不存在");
        }

        String studentXml =
                "<student><Sno>" + escapeXml(stu.sno()) +
                        "</Sno><Snm>" + escapeXml(stu.snm()) +
                        "</Snm><Sex>" + escapeXml(stu.sex()) +
                        "</Sex><Sde>" + escapeXml(stu.sde()) +
                        "</Sde></student>";

        String classXml =
                "<class><Cno>" + escapeXml(cno) + "</Cno></class>";

        String requestXml =
                "<crossSelectRequest><targetDept>" +
                        escapeXml(targetDept) +
                        "</targetDept>" +
                        studentXml +
                        classXml +
                        "</crossSelectRequest>";

        String body = sendPost(integrationCrossSelectUrl, requestXml);

        int msgStart = body.indexOf("<message>");
        int msgEnd = body.indexOf("</message>");

        if (msgStart >= 0 && msgEnd > msgStart) {
            return body.substring(msgStart + 9, msgEnd);
        }

        return body;
    }

    private String doCrossDrop(String sno,
                               String cno,
                               String targetDept) throws Exception {

        Student stu = studentDao.findById(sno);

        if (stu == null) {
            throw new IllegalStateException("学生不存在");
        }

        String studentXml =
                "<student><Sno>" + escapeXml(stu.sno()) +
                        "</Sno><Snm>" + escapeXml(stu.snm()) +
                        "</Snm><Sex>" + escapeXml(stu.sex()) +
                        "</Sex><Sde>" + escapeXml(stu.sde()) +
                        "</Sde></student>";

        String classXml =
                "<class><Cno>" + escapeXml(cno) + "</Cno></class>";

        String requestXml =
                "<crossDropRequest><targetDept>" +
                        escapeXml(targetDept) +
                        "</targetDept>" +
                        studentXml +
                        classXml +
                        "</crossDropRequest>";

        String body = sendPost(integrationCrossDropUrl, requestXml);

        int msgStart = body.indexOf("<message>");
        int msgEnd = body.indexOf("</message>");

        if (msgStart >= 0 && msgEnd > msgStart) {
            return body.substring(msgStart + 9, msgEnd);
        }

        return body;
    }

    private String fetchStatistics() throws Exception {

        String xml = sendGet(integrationStatisticsUrl);

        return parseStatisticsXml(xml);
    }

    private String parseStatisticsXml(String xml) throws Exception {

        StringBuilder sb = new StringBuilder();

        SAXReader reader = new SAXReader();

        Document doc = reader.read(new StringReader(xml));

        Element root = doc.getRootElement();

        Element summary =
                (Element) root.selectSingleNode("//*[local-name()='summary']");

        if (summary != null) {

            sb.append("总学生数: ")
                    .append(text(summary, "totalStudents"))
                    .append("\n");

            sb.append("总课程数: ")
                    .append(text(summary, "totalCourses"))
                    .append("\n");

            sb.append("总选课数: ")
                    .append(text(summary, "totalChoices"))
                    .append("\n");
        }

        return sb.toString();
    }

    private String sendPost(String urlStr,
                            String body) throws IOException {

        URL url = new URL(urlStr);

        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");

        conn.setRequestProperty(
                "Content-Type",
                "application/xml; charset=utf-8"
        );

        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);

        conn.setDoOutput(true);

        try (OutputStreamWriter writer =
                     new OutputStreamWriter(
                             conn.getOutputStream(),
                             "UTF-8")) {

            writer.write(body);
        }

        int status = conn.getResponseCode();

        if (status >= 400) {

            throw new IOException(
                    "HTTP " + status + ": " +
                            readStream(conn.getErrorStream())
            );
        }

        return readStream(conn.getInputStream());
    }

    private String sendGet(String urlStr) throws IOException {

        URL url = new URL(urlStr);

        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");

        conn.setRequestProperty(
                "Accept",
                "application/xml"
        );

        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        int status = conn.getResponseCode();

        if (status >= 400) {

            throw new IOException(
                    "HTTP " + status + ": " +
                            readStream(conn.getErrorStream())
            );
        }

        return readStream(conn.getInputStream());
    }

    private String readStream(java.io.InputStream is)
            throws IOException {

        if (is == null) return "";

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                is,
                                StandardCharsets.UTF_8
                        )
                );

        StringBuilder sb = new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }

    private static String text(Element parent,
                               String... names) {

        for (String name : names) {

            Element child = parent.element(name);

            if (child != null) {

                String text = child.getTextTrim();

                if (!text.isEmpty()) {
                    return text;
                }
            }
        }

        return null;
    }

    private static String escapeXml(String s) {

        if (s == null) return "";

        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private static class SharedCourse {

        final String id;
        final String name;
        final String score;
        final String teacher;
        final String location;
        final String source;

        SharedCourse(String id,
                     String name,
                     String score,
                     String teacher,
                     String location,
                     String source) {

            this.id = id;
            this.name = name;
            this.score = score;
            this.teacher = teacher;
            this.location = location;
            this.source = source;
        }
    }
}