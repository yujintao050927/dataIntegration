package com.educ.departmentA.dao;

import com.educ.departmentA.model.Course;
import com.educ.departmentA.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM Course";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Course course = new Course(
                    rs.getString("course_id"),
                    rs.getString("course_name"),
                    rs.getString("credit"),
                    rs.getString("teacher"),
                    rs.getString("location"),
                    rs.getString("is_shared")
                );
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
    
    public List<Course> getSharedCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM Course WHERE is_shared = 'Y'";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Course course = new Course(
                    rs.getString("course_id"),
                    rs.getString("course_name"),
                    rs.getString("credit"),
                    rs.getString("teacher"),
                    rs.getString("location"),
                    rs.getString("is_shared")
                );
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
    
    public Course getCourseById(String courseId) {
        String sql = "SELECT * FROM Course WHERE course_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Course(
                    rs.getString("course_id"),
                    rs.getString("course_name"),
                    rs.getString("credit"),
                    rs.getString("teacher"),
                    rs.getString("location"),
                    rs.getString("is_shared")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
