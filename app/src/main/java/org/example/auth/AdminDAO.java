package org.example.auth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {
    private Connection getConnection() throws SQLException {
        String url = System.getenv("SUPABASE_DB_URL");
        String user = System.getenv("SUPABASE_DB_USER");
        String password = System.getenv("SUPABASE_DB_PASSWORD");

        if (url == null || user == null || password == null
                || url.isBlank() || user.isBlank() || password.isBlank()) {
            throw new SQLException("Set SUPABASE_DB_URL, SUPABASE_DB_USER and SUPABASE_DB_PASSWORD.");
        }
        return DriverManager.getConnection(url, user, password);
    }

    public boolean hasAnyAdmin() throws SQLException {
        String sql = "SELECT EXISTS (SELECT 1 FROM public.admin_users)";
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            return r.next() && r.getBoolean(1);
        }
    }

    public boolean createAdmin(String username, String saltHex, String hashHex) throws SQLException {
        String sql = """
            INSERT INTO public.admin_users (username, password_salt, password_hash)
            VALUES (?, ?, ?)
            ON CONFLICT (username) DO NOTHING
            """;
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);
            p.setString(2, saltHex);
            p.setString(3, hashHex);
            return p.executeUpdate() == 1;
        }
    }

    public String getSalt(String username) throws SQLException {
        String sql = "SELECT password_salt FROM public.admin_users WHERE lower(username) = lower(?)";
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? r.getString(1) : null;
            }
        }
    }

    public boolean verifyAdmin(String username, String saltHex, String hashHex) throws SQLException {
        String sql = """
            SELECT password_salt, password_hash
            FROM public.admin_users
            WHERE lower(username) = lower(?)
            """;
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);
            try (ResultSet r = p.executeQuery()) {
                return r.next()
                    && PasswordUtil.constantTimeEquals(r.getString("password_salt"), saltHex)
                    && PasswordUtil.constantTimeEquals(r.getString("password_hash"), hashHex);
            }
        }
    }
}
