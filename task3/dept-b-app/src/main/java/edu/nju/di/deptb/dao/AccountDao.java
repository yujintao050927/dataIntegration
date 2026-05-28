package edu.nju.di.deptb.dao;

import edu.nju.di.deptb.DbProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class AccountDao {
    private final DbProvider db;

    public AccountDao(DbProvider db) {
        this.db = db;
    }

    public LoginResult login(String acc, String passwd) throws Exception {
        String sql = "SELECT ROLE, SNO FROM B_ACCOUNT WHERE ACC=? AND PASSWD=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, acc);
            ps.setString(2, passwd);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                int role = rs.getInt(1);
                String sno = rs.getString(2);
                return new LoginResult(role, sno);
            }
        }
    }

    public record LoginResult(int role, String sno) {
        public boolean isAdmin() {
            return role == 0;
        }
    }
}
