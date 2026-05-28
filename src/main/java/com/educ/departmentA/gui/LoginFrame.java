package com.educ.departmentA.gui;

import com.educ.departmentA.dao.StudentDAO;
import com.educ.departmentA.model.Student;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField accountField;
    private JPasswordField passwordField;
    private StudentDAO studentDAO;

    public LoginFrame() {
        studentDAO = new StudentDAO();
        initUI();
    }

    private void initUI() {
        setTitle("院系A - 教务管理系统 - 登录");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("院系A教务管理系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 登录表单
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 账号
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("账号:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        accountField = new JTextField(15);
        formPanel.add(accountField, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("密码:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton loginButton = new JButton("登录");
        JButton cancelButton = new JButton("取消");

        loginButton.setPreferredSize(new Dimension(100, 30));
        cancelButton.setPreferredSize(new Dimension(100, 30));

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void login() {
        String account = accountField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (account.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入账号和密码！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (studentDAO.login(account, password)) {
            boolean isAdmin = studentDAO.isAdmin(account);
            Student student = null;
            
            if (!isAdmin) {
                student = studentDAO.getStudentById(account);
            }

            this.dispose();
            MainFrame mainFrame = new MainFrame(isAdmin, student);
            mainFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "账号或密码错误！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }
}
