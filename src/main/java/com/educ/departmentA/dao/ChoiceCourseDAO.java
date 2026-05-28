package com.educ.departmentA.dao;

import com.educ.departmentA.model.ChoiceCourse;
import com.educ.departmentA.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChoiceCourseDAO {
    
    public List<ChoiceCourse> getCoursesByStudentId(String studentId) {
        List<ChoiceCourse> choices = new ArrayList<>();
        String sql = "SELECT * FROM ChoiceCourse WHERE student_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ChoiceCourse choice = new ChoiceCourse(
                    rs.getString("course_id"),
                    rs.getString("student_id"),
                    rs.getString("score")
                );
                choices.add(choice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return choices;
    }
    
    public boolean addChoiceCourse(String courseId, String studentId) {
        String sql = "INSERT INTO ChoiceCourse (course_id, student_id, score) VALUES (?, ?, NULL)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, courseId);
            pstmt.setString(2, studentId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean dropChoiceCourse(String courseId, String studentId) {
        String sql = "DELETE FROM ChoiceCourse WHERE course_id = ? AND student_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, courseId);
            pstmt.setString(2, studentId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean hasChoiced(String courseId, String studentId) {
        String sql = "SELECT * FROM ChoiceCourse WHERE course_id = ? AND student_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, courseId);
            pstmt.setString(2, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
