package edu.nju.di.deptb.dao;

import edu.nju.di.deptb.DbProvider;
import edu.nju.di.deptb.model.Choice;

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
        String sql = "INSERT INTO B_CHOICE(SNO, CNO, SCORE) VALUES (?,?,NULL)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sno);
            ps.setString(2, cno);
            ps.executeUpdate();
        }
    }

    public void deleteChoice(String sno, String cno) throws Exception {
        String sql = "DELETE FROM B_CHOICE WHERE SNO=? AND CNO=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sno);
            ps.setString(2, cno);
            ps.executeUpdate();
        }
    }

    public List<Choice> listByStudent(String sno) throws Exception {
        String sql = "SELECT SNO, CNO, SCORE FROM B_CHOICE WHERE SNO=? ORDER BY CNO";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sno);
            try (ResultSet rs = ps.executeQuery()) {
                List<Choice> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new Choice(rs.getString(1), rs.getString(2), rs.getString(3)));
                }
                return out;
            }
        }
    }

    public List<Choice> listAll() throws Exception {
        String sql = "SELECT SNO, CNO, SCORE FROM B_CHOICE ORDER BY SNO, CNO";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Choice> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new Choice(rs.getString(1), rs.getString(2), rs.getString(3)));
            }
            return out;
        }
    }
}
