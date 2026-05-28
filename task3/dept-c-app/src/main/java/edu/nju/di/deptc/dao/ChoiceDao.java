package edu.nju.di.deptc.dao;

import edu.nju.di.deptc.DbProvider;
import edu.nju.di.deptc.model.Choice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public final class ChoiceDao {
    private final DbProvider db;

    public ChoiceDao(DbProvider db) {
        this.db = db;
    }

    public void addChoice(String sno, String cno) throws Exception {
        String sql = "INSERT INTO ChoiceCourse_C(cno, sno, grd) VALUES (?,?,NULL)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cno);
            ps.setString(2, sno);
            ps.executeUpdate();
        }
    }

    public void deleteChoice(String sno, String cno) throws Exception {
        String sql = "DELETE FROM ChoiceCourse_C WHERE sno=? AND cno=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sno);
            ps.setString(2, cno);
            ps.executeUpdate();
        }
    }

    public List<Choice> listByStudent(String sno) throws Exception {
        String sql = "SELECT cno, sno, grd FROM ChoiceCourse_C WHERE sno=? ORDER BY cno";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sno);
            try (ResultSet rs = ps.executeQuery()) {
                List<Choice> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new Choice(rs.getString(2), rs.getString(1), rs.getString(3)));
                }
                return out;
            }
        }
    }

    public List<Choice> listAll() throws Exception {
        String sql = "SELECT cno, sno, grd FROM ChoiceCourse_C ORDER BY sno, cno";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Choice> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new Choice(rs.getString(2), rs.getString(1), rs.getString(3)));
            }
            return out;
        }
    }
}
