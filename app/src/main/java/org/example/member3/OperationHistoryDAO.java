
package org.example.member3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.example.models.AllocationHistory;

public class OperationHistoryDAO {

    private Connection getConnection() throws SQLException {
        String url = System.getenv("SUPABASE_DB_URL");
        String user = System.getenv("SUPABASE_DB_USER");
        String password = System.getenv("SUPABASE_DB_PASSWORD");

        if (url == null || user == null || password == null) {
            throw new SQLException(
                    "Database environment variables are missing.");
        }

        return DriverManager.getConnection(url, user, password);
    }

    public boolean saveHistory(
            String examId,
            String operationType,
            String studentUSN1,
            String studentUSN2,
            String oldSeatId1,
            String oldSeatId2,
            String newSeatId1,
            String newSeatId2) {

        String sql = """
            INSERT INTO operation_history
                (exam_id, operation_type,
                 student_usn1, student_usn2,
                 old_seat_id1, old_seat_id2,
                 new_seat_id1, new_seat_id2)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, examId);
            ps.setString(2, operationType);
            ps.setString(3, studentUSN1);
            ps.setString(4, studentUSN2);
            ps.setString(5, oldSeatId1);
            ps.setString(6, oldSeatId2);
            ps.setString(7, newSeatId1);
            ps.setString(8, newSeatId2);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println(
                    "Error saving operation history: " + e.getMessage());
            return false;
        }
    }

    public List<AllocationHistory> getAllHistory() {
        String sql = """
            SELECT * FROM operation_history
            ORDER BY performed_at DESC, history_id DESC
            """;

        return loadHistory(sql, null);
    }

    public List<AllocationHistory> getHistoryByExam(String examId) {
        String sql = """
            SELECT * FROM operation_history
            WHERE exam_id = ?
            ORDER BY performed_at DESC, history_id DESC
            """;

        return loadHistory(sql, examId);
    }

    public List<AllocationHistory> getHistoryByStudent(String usn) {
        String sql = """
            SELECT * FROM operation_history
            WHERE LOWER(student_usn1) = LOWER(?)
               OR LOWER(student_usn2) = LOWER(?)
            ORDER BY performed_at DESC, history_id DESC
            """;

        List<AllocationHistory> history = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usn);
            ps.setString(2, usn);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    history.add(mapHistory(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error retrieving student history: " + e.getMessage());
        }

        return history;
    }

    private List<AllocationHistory> loadHistory(
            String sql, String parameter) {

        List<AllocationHistory> history = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (parameter != null) {
                ps.setString(1, parameter);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    history.add(mapHistory(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error retrieving operation history: " + e.getMessage());
        }

        return history;
    }

    private AllocationHistory mapHistory(ResultSet rs)
            throws SQLException {

        Timestamp timestamp = rs.getTimestamp("performed_at");

        return new AllocationHistory(
                rs.getInt("history_id"),
                rs.getString("exam_id"),
                rs.getString("operation_type"),
                rs.getString("student_usn1"),
                rs.getString("student_usn2"),
                rs.getString("old_seat_id1"),
                rs.getString("old_seat_id2"),
                rs.getString("new_seat_id1"),
                rs.getString("new_seat_id2"),
                timestamp != null
                        ? timestamp.toLocalDateTime()
                        : null
        );
    }
}
