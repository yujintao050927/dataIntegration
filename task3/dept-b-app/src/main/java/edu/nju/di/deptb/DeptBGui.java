package edu.nju.di.deptb;

import edu.nju.di.deptb.dao.AccountDao;
import edu.nju.di.deptb.dao.ChoiceDao;
import edu.nju.di.deptb.dao.CourseDao;
import edu.nju.di.deptb.dao.StudentDao;
import edu.nju.di.deptb.model.Choice;
import edu.nju.di.deptb.model.Course;
import edu.nju.di.deptb.model.Student;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

public final class DeptBGui {
    private final AccountDao accountDao;
    private final StudentDao studentDao;
    private final CourseDao courseDao;
    private final ChoiceDao choiceDao;

    public DeptBGui(AccountDao accountDao, StudentDao studentDao, CourseDao courseDao, ChoiceDao choiceDao) {
        this.accountDao = accountDao;
        this.studentDao = studentDao;
        this.courseDao = courseDao;
        this.choiceDao = choiceDao;
    }

    public void showLogin() {
        JFrame f = new JFrame("院系B - 登录");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        JTextField acc = new JTextField();
        JPasswordField pwd = new JPasswordField();
        form.add(new JLabel("账号", SwingConstants.RIGHT));
        form.add(acc);
        form.add(new JLabel("密码", SwingConstants.RIGHT));
        form.add(pwd);

        JButton login = new JButton("登录");
        form.add(new JLabel(""));
        form.add(login);

        login.addActionListener(e -> {
            try {
                String a = acc.getText().trim();
                String p = new String(pwd.getPassword());
                AccountDao.LoginResult r = accountDao.login(a, p);
                if (r == null) {
                    JOptionPane.showMessageDialog(f, "账号或密码错误");
                    return;
                }
                f.dispose();
                if (r.isAdmin()) {
                    showAdmin();
                } else {
                    if (r.sno() == null || r.sno().isBlank()) {
                        JOptionPane.showMessageDialog(null, "学生账号缺少学号绑定（B_ACCOUNT.SNO为空）");
                        showLogin();
                        return;
                    }
                    showStudent(r.sno());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "登录失败: " + ex.getMessage());
            }
        });

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.add(form, BorderLayout.CENTER);
        f.setContentPane(root);
        f.setSize(new Dimension(360, 180));
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private void showStudent(String sno) {
        JFrame f = new JFrame("院系B - 学生(" + sno + ")");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        DefaultTableModel courseModel = new DefaultTableModel(new Object[]{"课程号", "课程名", "学分", "教师", "地点"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable courseTable = new JTable(courseModel);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableModel choiceModel = new DefaultTableModel(new Object[]{"课程号", "得分"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable choiceTable = new JTable(choiceModel);
        choiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton refresh = new JButton("刷新");
        JButton select = new JButton("选课");
        JButton drop = new JButton("退课");
        JButton logout = new JButton("退出登录");

        Runnable reload = () -> {
            try {
                courseModel.setRowCount(0);
                List<Course> courses = courseDao.listAll();
                for (Course c : courses) {
                    courseModel.addRow(new Object[]{c.cno(), c.name(), c.credit(), c.teacher(), c.location()});
                }

                choiceModel.setRowCount(0);
                List<Choice> choices = choiceDao.listByStudent(sno);
                for (Choice c : choices) {
                    choiceModel.addRow(new Object[]{c.cno(), c.score()});
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "加载失败: " + ex.getMessage());
            }
        };

        refresh.addActionListener(e -> reload.run());

        select.addActionListener(e -> {
            int row = courseTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(f, "请选择一门课程");
                return;
            }
            String cno = String.valueOf(courseModel.getValueAt(row, 0));
            try {
                choiceDao.addChoice(sno, cno);
                reload.run();
                JOptionPane.showMessageDialog(f, "选课成功: " + cno);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "选课失败: " + ex.getMessage());
            }
        });

        drop.addActionListener(e -> {
            int row = choiceTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(f, "请选择一条已选课程");
                return;
            }
            String cno = String.valueOf(choiceModel.getValueAt(row, 0));
            try {
                choiceDao.deleteChoice(sno, cno);
                reload.run();
                JOptionPane.showMessageDialog(f, "退课成功: " + cno);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "退课失败: " + ex.getMessage());
            }
        });

        logout.addActionListener(e -> {
            f.dispose();
            showLogin();
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(refresh);
        buttons.add(select);
        buttons.add(drop);
        buttons.add(logout);

        JPanel tables = new JPanel(new GridLayout(1, 2, 8, 8));
        tables.add(new JScrollPane(courseTable));
        tables.add(new JScrollPane(choiceTable));

        f.setLayout(new BorderLayout(8, 8));
        f.add(buttons, BorderLayout.NORTH);
        f.add(tables, BorderLayout.CENTER);

        f.setSize(new Dimension(960, 520));
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        reload.run();
    }

    private void showAdmin() {
        JFrame f = new JFrame("院系B - 管理员");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        DefaultTableModel studentModel = new DefaultTableModel(new Object[]{"学号", "姓名", "性别", "专业", "院系"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        DefaultTableModel courseModel = new DefaultTableModel(new Object[]{"课程号", "课程名", "学时", "学分", "教师", "地点", "属性"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        DefaultTableModel choiceModel = new DefaultTableModel(new Object[]{"学号", "课程号", "得分"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable studentTable = new JTable(studentModel);
        JTable courseTable = new JTable(courseModel);
        JTable choiceTable = new JTable(choiceModel);

        JButton refresh = new JButton("刷新");
        JButton logout = new JButton("退出登录");

        Runnable reload = () -> {
            try {
                studentModel.setRowCount(0);
                for (Student s : studentDao.listAll()) {
                    studentModel.addRow(new Object[]{s.sno(), s.name(), s.sex(), s.major(), s.dept()});
                }

                courseModel.setRowCount(0);
                for (Course c : courseDao.listAll()) {
                    courseModel.addRow(new Object[]{c.cno(), c.name(), c.hours(), c.credit(), c.teacher(), c.location(), c.attr()});
                }

                choiceModel.setRowCount(0);
                for (Choice c : choiceDao.listAll()) {
                    choiceModel.addRow(new Object[]{c.sno(), c.cno(), c.score()});
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "加载失败: " + ex.getMessage());
            }
        };

        refresh.addActionListener(e -> reload.run());
        logout.addActionListener(e -> {
            f.dispose();
            showLogin();
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(refresh);
        top.add(logout);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("学生", new JScrollPane(studentTable));
        tabs.addTab("课程", new JScrollPane(courseTable));
        tabs.addTab("选课", new JScrollPane(choiceTable));

        f.setLayout(new BorderLayout(8, 8));
        f.add(top, BorderLayout.NORTH);
        f.add(tabs, BorderLayout.CENTER);

        f.setSize(new Dimension(1020, 560));
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        reload.run();
    }
}
