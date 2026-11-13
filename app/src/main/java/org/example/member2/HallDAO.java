package org.example.member2;

import java.sql.*;

public class HallDAO {
    private static final String URL = "jdbc:postgresql://db.YOUR_PROJECT_REF.supabase.co:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "YOUR_SUPABASE_PASSWORD";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public boolean insertHall(String hallId, String hallName, int capacity) {
        String query = "INSERT INTO halls (hall_id, hall_name, capacity) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, hallId);
            pstmt.setString(2, hallName);
            pstmt.setInt(3, capacity);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
