
package org.example.member2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.example.member1.Student;

public class ExamAllocationDAO {

    private Connection getConnection() throws SQLException {
        String url = System.getenv("SUPABASE_DB_URL");
        String user = System.getenv("SUPABASE_DB_USER");
        String password = System.getenv("SUPABASE_DB_PASSWORD");

        if (url == null || user == null || password == null) {
            throw new SQLException("Database environment variables are missing.");
        }

        return DriverManager.getConnection(url, user, password);
    }

    // Holds all data loaded for one examination
    public static class ExamData {
        public List<Hall> halls = new ArrayList<>();
        public List<Student> students = new ArrayList<>();
        public int allocatedCount;
    }

    public List<String[]> getAllExams() {
        List<String[]> exams = new ArrayList<>();

        String sql = """
            SELECT exam_id, exam_name, exam_date, session
            FROM examinations
            ORDER BY exam_date
            """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                exams.add(new String[]{
                    rs.getString("exam_id"),
                    rs.getString("exam_name"),
                    rs.getString("exam_date"),
                    rs.getString("session")
                });
            }

        } catch (SQLException e) {
            System.out.println("Error loading examinations: " + e.getMessage());
        }

        return exams;
    }

    // Load halls, students and saved allocations using ONE connection
    public ExamData loadExamData(String examId) {

        ExamData data = new ExamData();

        try (Connection con = getConnection()) {

            // 1. Load halls
            String hallSql = """
                SELECT h.hall_id, h.hall_name,
                       h.number_of_rows, h.seats_per_row
                FROM halls h
                JOIN exam_halls eh ON h.hall_id = eh.hall_id
                WHERE eh.exam_id = ?
                """;

            try (PreparedStatement ps = con.prepareStatement(hallSql)) {
                ps.setString(1, examId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        data.halls.add(new Hall(
                            rs.getString("hall_id"),
                            rs.getString("hall_name"),
                            rs.getInt("number_of_rows"),
                            rs.getInt("seats_per_row")
                        ));
                    }
                }
            }

            // 2. Load registered students
            String studentSql = """
                SELECT DISTINCT s.usn, s.name, s.branch,
                       s.semester, s.section
                FROM students s
                JOIN exam_registrations er
                    ON s.student_id = er.student_id
                WHERE er.exam_id = ?
                ORDER BY s.usn
                """;

            try (PreparedStatement ps = con.prepareStatement(studentSql)) {
                ps.setString(1, examId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        data.students.add(new Student(
                            rs.getString("usn"),
                            rs.getString("name"),
                            rs.getString("branch"),
                            rs.getInt("semester"),
                            rs.getString("section")
                        ));
                    }
                }
            }

            // 3. Load saved seat allocations
            String allocationSql = """
                SELECT s.hall_id, s.row_number,
                       s.column_number, st.usn
                FROM allocations a
                JOIN exam_registrations er
                    ON a.registration_id = er.registration_id
                JOIN students st
                    ON er.student_id = st.student_id
                JOIN seats s
                    ON a.seat_id = s.seat_id
                WHERE a.exam_id = ?
                  AND a.status = 'allocated'
                """;

            try (PreparedStatement ps = con.prepareStatement(allocationSql)) {
                ps.setString(1, examId);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        String hallId = rs.getString("hall_id");
                        int rowNumber = rs.getInt("row_number");
                        int columnNumber = rs.getInt("column_number");
                        String usn = rs.getString("usn");

                        for (Hall hall : data.halls) {

                            if (!hall.getHallId().equals(hallId)) {
                                continue;
                            }

                            RowNode row = hall.getRows().getHead();

                            while (row != null) {

                                if (row.getRowNumber() == rowNumber) {

                                    SeatNode seat =
                                        row.getSeatList().getHead();

                                    while (seat != null) {

                                        if (seat.getSeatNumber() == columnNumber) {
                                            seat.setAssignedUsn(usn);
                                            data.allocatedCount++;
                                            break;
                                        }

                                        seat = seat.getNext();
                                    }

                                    break;
                                }

                                row = row.getNext();
                            }

                            break;
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error loading examination data: "
                    + e.getMessage());
        }

        return data;
    }

    // Existing methods retained for compatibility
    public List<Hall> getHallsForExam(String examId) {
        return loadExamData(examId).halls;
    }

    public List<Student> getRegisteredStudents(String examId) {
        return loadExamData(examId).students;
    }

    public int loadSavedAllocations(String examId, List<Hall> halls) {
        ExamData data = loadExamData(examId);

        for (Hall target : halls) {
            for (Hall loaded : data.halls) {
                if (target.getHallId().equals(loaded.getHallId())) {
                    RowNode row = loaded.getRows().getHead();

                    while (row != null) {
                        SeatNode seat = row.getSeatList().getHead();

                        while (seat != null) {
                            if (seat.isOccupied()) {
                                RowNode targetRow = target.getRows().getHead();

                                while (targetRow != null) {
                                    if (targetRow.getRowNumber() == row.getRowNumber()) {
                                        SeatNode targetSeat = targetRow.getSeatList().getHead();

                                        while (targetSeat != null) {
                                            if (targetSeat.getSeatNumber() == seat.getSeatNumber()) {
                                                targetSeat.setAssignedUsn(seat.getAllocatedUsn());
                                                break;
                                            }
                                            targetSeat = targetSeat.getNext();
                                        }
                                        break;
                                    }
                                    targetRow = targetRow.getNext();
                                }
                            }
                            seat = seat.getNext();
                        }
                        row = row.getNext();
                    }
                }
            }
        }

        return data.allocatedCount;
    }
}
