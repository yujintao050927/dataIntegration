package edu.nju.di.deptc.dao;

import edu.nju.di.deptc.DbProvider;
import edu.nju.di.deptc.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public final class StudentDao {
    private final DbProvider db;

    public StudentDao(DbProvider db) {
        this.db = db;
    }

    public Student findById(String sno) throws Exception {
        String sql = "SELECT sno, snm, sex, sde, pwd FROM Student_C WHERE sno=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sno);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Student(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
            }
        }
    }

    public void insertIfAbsent(Student s) throws Exception {
        if (findById(s.sno()) != null) {
            return;
        }
        String sql = "INSERT INTO Student_C(sno, snm, sex, sde, pwd) VALUES (?,?,?,?,?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.sno());
            ps.setString(2, s.snm());
            ps.setString(3, s.sex());
            ps.setString(4, s.sde());
            ps.setString(5, s.pwd() == null || s.pwd().trim().isEmpty() ? "123456" : s.pwd());
            ps.executeUpdate();
        }
    }

    public List<Student> listAll() throws Exception {
        String sql = "SELECT sno, snm, sex, sde, pwd FROM Student_C ORDER BY sno";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Student> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new Student(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
            }
            return out;
        }
    }
}
