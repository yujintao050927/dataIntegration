package edu.nju.di.deptc.dao;

import edu.nju.di.deptc.DbProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class AccountDao {
    private final DbProvider db;

    public AccountDao(DbProvider db) {
        this.db = db;
    }

    public LoginResult login(String acc, String passwd) throws Exception {
        // 首先检查管理员账号
        String adminSql = "SELECT acc FROM Account_C WHERE acc=? AND passwd=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(adminSql)) {
            ps.setString(1, acc);
            ps.setString(2, passwd);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String account = rs.getString(1);
                    boolean isAdmin = "admin".equalsIgnoreCase(account);
                    String sno = isAdmin ? null : account;
                    return new LoginResult(isAdmin, sno);
                }
            }
        }

        // 如果不是管理员，检查学生账号
        String studentSql = "SELECT sno FROM Student_C WHERE sno=? AND pwd=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(studentSql)) {
            ps.setString(1, acc);
            ps.setString(2, passwd);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String sno = rs.getString(1);
                    return new LoginResult(false, sno);
                }
            }
        }

        return null;
    }

    public static class LoginResult {
        private final boolean isAdmin;
        private final String sno;

        public LoginResult(boolean isAdmin, String sno) {
            this.isAdmin = isAdmin;
            this.sno = sno;
        }

        public boolean isAdmin() {
            return isAdmin;
        }

        public String sno() {
            return sno;
        }
    }
}
