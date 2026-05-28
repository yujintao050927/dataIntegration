package edu.nju.di.deptb.dao;

import edu.nju.di.deptb.DbProvider;
import edu.nju.di.deptb.model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public final class CourseDao {
    private final DbProvider db;

    public CourseDao(DbProvider db) {
        this.db = db;
    }

    public Course findById(String cno) throws Exception {
        String sql = "SELECT CNO, CNM, HOURS, CREDIT, TEACHER, LOCATION, ATTR FROM B_COURSE WHERE CNO=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cno);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Course(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7));
            }
        }
    }

    public List<Course> listAll() throws Exception {
        String sql = "SELECT CNO, CNM, HOURS, CREDIT, TEACHER, LOCATION, ATTR FROM B_COURSE ORDER BY CNO";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Course> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new Course(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)));
            }
            return out;
        }
    }
}
