
package org.example.member2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class AllocationDAO {

    private Connection getConnection() throws SQLException {
        String url = System.getenv("SUPABASE_DB_URL");
        String user = System.getenv("SUPABASE_DB_USER");
        String password = System.getenv("SUPABASE_DB_PASSWORD");

        if (url == null || user == null || password == null) {
            throw new SQLException("Database environment variables are missing.");
        }

        return DriverManager.getConnection(url, user, password);
    }

    public boolean saveAllocation(
            String examId, String hallId,
            String usn, int rowNumber, int seatNumber) {

        String query = """
            INSERT INTO allocations
                (allocation_id, registration_id, exam_id, seat_id)
            SELECT ?, er.registration_id, ?, s.seat_id
            FROM exam_registrations er
            JOIN students st ON st.student_id = er.student_id
            JOIN seats s ON s.hall_id = ?
                AND s.row_number = ?
                AND s.column_number = ?
            WHERE st.usn = ?
              AND er.exam_id = ?
            ON CONFLICT (registration_id)
            DO UPDATE SET
                seat_id = EXCLUDED.seat_id,
                status = 'allocated'
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, examId);
            pstmt.setString(3, hallId);
            pstmt.setInt(4, rowNumber);
            pstmt.setInt(5, seatNumber);
            pstmt.setString(6, usn);
            pstmt.setString(7, examId);

           int rows = pstmt.executeUpdate();

System.out.println("Rows inserted/updated: " + rows);

return rows > 0;

} catch (SQLException e) {
    e.printStackTrace();
    return false;
}
    }
}
