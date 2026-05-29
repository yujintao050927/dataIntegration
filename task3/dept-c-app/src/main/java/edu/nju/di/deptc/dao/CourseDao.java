package edu.nju.di.deptc.dao;

import edu.nju.di.deptc.DbProvider;
import edu.nju.di.deptc.model.Course;

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
        String sql = "SELECT cno, cnm, ctm, cpt, tec, pla, share_flag FROM Course_C WHERE cno=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cno);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Course(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getInt(4),
                        rs.getString(5), rs.getString(6), rs.getString(7));
            }
        }
    }

    public List<Course> listAll() throws Exception {
        String sql = "SELECT cno, cnm, ctm, cpt, tec, pla, share_flag FROM Course_C ORDER BY cno";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Course> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new Course(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getInt(4),
                        rs.getString(5), rs.getString(6), rs.getString(7)));
            }
            return out;
        }
    }

    public List<Course> listShared() throws Exception {
        String sql = "SELECT cno, cnm, ctm, cpt, tec, pla, share_flag FROM Course_C WHERE share_flag='Y' ORDER BY cno";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Course> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new Course(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getInt(4),
                        rs.getString(5), rs.getString(6), rs.getString(7)));
            }
            return out;
        }
    }

    public void insertIfAbsent(Course c) throws Exception {
        if (findById(c.cno()) != null) {
            return;
        }
        String sql = "INSERT INTO Course_C(cno, cnm, ctm, cpt, tec, pla, share_flag) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.cno());
            ps.setString(2, c.cnm());
            ps.setInt(3, Integer.parseInt(c.cde()));
            ps.setInt(4, c.credit() != null ? c.credit() : 0);
            ps.setString(5, c.tea());
            ps.setString(6, "");
            ps.setString(7, "N");
            ps.executeUpdate();
        }
    }
}