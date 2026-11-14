
package org.example.member3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.example.models.Operation;
import org.example.models.OperationResult;
import org.example.models.SeatLocation;
import org.example.services.AllocationService;

public class OperationsDAO implements AllocationService {

    private Connection getConnection() throws SQLException {
        String url = System.getenv("SUPABASE_DB_URL");
        String user = System.getenv("SUPABASE_DB_USER");
        String password = System.getenv("SUPABASE_DB_PASSWORD");

        if (url == null || user == null || password == null) {
            throw new SQLException("Database environment variables are missing.");
        }

        return DriverManager.getConnection(url, user, password);
    }

    private static class AllocationData {
        String allocationId;
        String registrationId;
        String examId;
        String seatId;
        String status;

        AllocationData(ResultSet rs) throws SQLException {
            allocationId = rs.getString("allocation_id");
            registrationId = rs.getString("registration_id");
            examId = rs.getString("exam_id");
            seatId = rs.getString("seat_id");
            status = rs.getString("status");
        }
    }

    private AllocationData getAllocation(
            Connection conn, String examId, String usn) throws SQLException {

        String sql = """
            SELECT a.allocation_id, a.registration_id,
                   a.exam_id, a.seat_id, a.status
            FROM allocations a
            JOIN exam_registrations er
                ON a.registration_id = er.registration_id
            JOIN students st
                ON er.student_id = st.student_id
            WHERE a.exam_id = ?
              AND st.usn = ?
              AND a.status = 'allocated'
            FOR UPDATE OF a
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, examId);
            ps.setString(2, usn);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AllocationData(rs);
                }
            }
        }

        return null;
    }

    @Override
    public OperationResult swapAllocations(
            String examId, String usn1, String usn2) {

        if (usn1 == null || usn2 == null || usn1.equals(usn2)) {
            return new OperationResult(false,
                    "Select two different students.");
        }

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try {
                AllocationData first =
                        getAllocation(conn, examId, usn1);

                AllocationData second =
                        getAllocation(conn, examId, usn2);

                if (first == null || second == null) {
                    conn.rollback();
                    return new OperationResult(false,
                            "Both students must have allocated seats.");
                }

                performSwap(conn, first, second);

                conn.commit();

                return new OperationResult(true,
                        "Seats swapped successfully.");

            } catch (SQLException e) {
                conn.rollback();
                return new OperationResult(false,
                        "Swap failed: " + e.getMessage());
            }

        } catch (SQLException e) {
            return new OperationResult(false,
                    "Database error: " + e.getMessage());
        }
    }

    private void performSwap(
            Connection conn,
            AllocationData first,
            AllocationData second) throws SQLException {

        // Temporarily remove the second allocation to avoid
        // violating the UNIQUE(exam_id, seat_id) constraint.
        String deleteSql =
                "DELETE FROM allocations WHERE allocation_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
            ps.setString(1, second.allocationId);
            ps.executeUpdate();
        }

        String updateSql = """
            UPDATE allocations
            SET seat_id = ?
            WHERE allocation_id = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setString(1, second.seatId);
            ps.setString(2, first.allocationId);

            if (ps.executeUpdate() != 1) {
                throw new SQLException("Could not update first allocation.");
            }
        }

        String insertSql = """
            INSERT INTO allocations
                (allocation_id, registration_id, exam_id, seat_id, status)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, second.allocationId);
            ps.setString(2, second.registrationId);
            ps.setString(3, second.examId);
            ps.setString(4, first.seatId);
            ps.setString(5, second.status);

            ps.executeUpdate();
        }
    }

    @Override
    public OperationResult reassignAllocation(
            String examId, String usn, String hallId, String seatId) {

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try {
                AllocationData current =
                        getAllocation(conn, examId, usn);

                if (current == null) {
                    conn.rollback();
                    return new OperationResult(false,
                            "Student has no allocated seat.");
                }

                if (current.seatId.equals(seatId)) {
                    conn.rollback();
                    return new OperationResult(false,
                            "Student is already in this seat.");
                }

                // Lock and validate the destination seat.
                String seatSql = """
                    SELECT s.seat_id
                    FROM seats s
                    JOIN exam_halls eh ON eh.hall_id = s.hall_id
                    WHERE s.seat_id = ?
                      AND s.hall_id = ?
                      AND eh.exam_id = ?
                      AND s.is_active = TRUE
                    FOR UPDATE OF s
                    """;

                try (PreparedStatement ps =
                        conn.prepareStatement(seatSql)) {

                    ps.setString(1, seatId);
                    ps.setString(2, hallId);
                    ps.setString(3, examId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return new OperationResult(false,
                                    "Invalid or inactive destination seat.");
                        }
                    }
                }

                // Check whether another student occupies the seat.
                String occupiedSql = """
                    SELECT allocation_id
                    FROM allocations
                    WHERE exam_id = ?
                      AND seat_id = ?
                      AND status = 'allocated'
                    """;

                try (PreparedStatement ps =
                        conn.prepareStatement(occupiedSql)) {

                    ps.setString(1, examId);
                    ps.setString(2, seatId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            conn.rollback();
                            return new OperationResult(false,
                                    "This seat is already occupied.");
                        }
                    }
                }

                String updateSql = """
                    UPDATE allocations
                    SET seat_id = ?
                    WHERE allocation_id = ?
                    """;

                try (PreparedStatement ps =
                        conn.prepareStatement(updateSql)) {

                    ps.setString(1, seatId);
                    ps.setString(2, current.allocationId);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Allocation update failed.");
                    }
                }

                conn.commit();

                return new OperationResult(true,
                        "Seat reassigned successfully.");

            } catch (SQLException e) {
                conn.rollback();
                return new OperationResult(false,
                        "Reassignment failed: " + e.getMessage());
            }

        } catch (SQLException e) {
            return new OperationResult(false,
                    "Database error: " + e.getMessage());
        }
    }

    @Override
    public boolean restoreSwap(Operation operation) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try {
                AllocationData first = getAllocation(
                        conn, operation.getExamId(),
                        operation.getStudentUSN1());

                AllocationData second = getAllocation(
                        conn, operation.getExamId(),
                        operation.getStudentUSN2());

                if (first == null || second == null
                        || !first.seatId.equals(operation.getNewSeatId1())
                        || !second.seatId.equals(operation.getNewSeatId2())) {
                    conn.rollback();
                    return false;
                }

                performSwap(conn, first, second);
                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean restoreReassignment(Operation operation) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try {
                AllocationData current = getAllocation(
                        conn, operation.getExamId(),
                        operation.getStudentUSN1());

                if (current == null
                        || !current.seatId.equals(operation.getNewSeatId1())) {
                    conn.rollback();
                    return false;
                }

                String updateSql = """
                    UPDATE allocations
                    SET seat_id = ?
                    WHERE allocation_id = ?
                      AND NOT EXISTS (
                          SELECT 1 FROM allocations other
                          WHERE other.exam_id = ?
                            AND other.seat_id = ?
                            AND other.status = 'allocated'
                            AND other.allocation_id <> ?
                      )
                    """;

                try (PreparedStatement ps =
                        conn.prepareStatement(updateSql)) {

                    ps.setString(1, operation.getOldSeatId1());
                    ps.setString(2, current.allocationId);
                    ps.setString(3, operation.getExamId());
                    ps.setString(4, operation.getOldSeatId1());
                    ps.setString(5, current.allocationId);

                    if (ps.executeUpdate() != 1) {
                        conn.rollback();
                        return false;
                    }
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public SeatLocation getCurrentSeat(String examId, String usn) {

        String sql = """
            SELECT s.hall_id, s.row_number,
                   s.column_number, s.seat_id
            FROM allocations a
            JOIN exam_registrations er
                ON a.registration_id = er.registration_id
            JOIN students st
                ON er.student_id = st.student_id
            JOIN seats s ON a.seat_id = s.seat_id
            WHERE a.exam_id = ?
              AND st.usn = ?
              AND a.status = 'allocated'
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, examId);
            ps.setString(2, usn);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new SeatLocation(
                            rs.getString("hall_id"),
                            rs.getInt("row_number"),
                            rs.getInt("column_number"),
                            rs.getString("seat_id"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
