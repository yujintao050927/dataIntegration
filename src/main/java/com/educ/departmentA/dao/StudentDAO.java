package com.educ.departmentA.dao;

import com.educ.departmentA.model.Student;
import com.educ.departmentA.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDAO {
    
    public Student getStudentById(String studentId) {
        String sql = "SELECT * FROM Student WHERE student_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Student(
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("gender"),
                    rs.getString("department"),
                    rs.getString("account_name")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean login(String accountName, String password) {
        String sql = "SELECT * FROM Account WHERE account_name = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountName);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean isAdmin(String accountName) {
        String sql = "SELECT * FROM Account WHERE account_name = ? AND permission = 'root'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountName);
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
