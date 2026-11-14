
package org.example.member2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HallDAO {

    private Connection getConnection() throws SQLException {

        String url = System.getenv("SUPABASE_DB_URL");
        String user = System.getenv("SUPABASE_DB_USER");
        String password = System.getenv("SUPABASE_DB_PASSWORD");

        if (url == null || url.isBlank()
                || user == null || user.isBlank()
                || password == null || password.isBlank()) {
            throw new SQLException("Database environment variables are missing.");
        }

        return DriverManager.getConnection(url, user, password);
    }

    // Test database connection
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Connected to Supabase successfully!");
            return true;
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }

    // Insert a new hall into the database
    public boolean insertHall(String hallId, String hallName,
                              int numberOfRows, int seatsPerRow) {

        if (numberOfRows <= 0 || seatsPerRow <= 0) {
            System.out.println("Rows and seats per row must be positive.");
            return false;
        }

        String query = """
            INSERT INTO halls
            (hall_id, hall_name, total_capacity, number_of_rows, seats_per_row)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, hallId);
            pstmt.setString(2, hallName);
            pstmt.setInt(3, numberOfRows * seatsPerRow);
            pstmt.setInt(4, numberOfRows);
            pstmt.setInt(5, seatsPerRow);

            pstmt.executeUpdate();
            System.out.println("Hall inserted successfully!");
            return true;

        } catch (SQLException e) {
            System.out.println("Could not insert hall: " + e.getMessage());
            return false;
        }
    }
}
