
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

        return DriverManager.getConnection(url, user, password);
    }

    // Retrieve all examinations
    public List<String[]> getAllExams() {
        List<String[]> exams = new ArrayList<>();

        String sql = "SELECT exam_id, exam_name, exam_date, session " +
                     "FROM examinations ORDER BY exam_date";

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

    // Retrieve halls assigned to a particular examination
    public List<Hall> getHallsForExam(String examId) {
        List<Hall> halls = new ArrayList<>();

        String sql = "SELECT h.hall_id, h.hall_name, " +
                     "h.number_of_rows, h.seats_per_row " +
                     "FROM halls h " +
                     "JOIN exam_halls eh ON h.hall_id = eh.hall_id " +
                     "WHERE eh.exam_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, examId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    halls.add(new Hall(
                        rs.getString("hall_id"),
                        rs.getString("hall_name"),
                        rs.getInt("number_of_rows"),
                        rs.getInt("seats_per_row")
                    ));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error loading halls: " + e.getMessage());
        }

        return halls;
    }

    // Retrieve only students registered for the selected examination
    public List<Student> getRegisteredStudents(String examId) {
        List<Student> students = new ArrayList<>();

        String sql = "SELECT DISTINCT s.usn, s.name, s.branch, " +
                     "s.semester, s.section " +
                     "FROM students s " +
                     "JOIN exam_registrations er " +
                     "ON s.student_id = er.student_id " +
                     "WHERE er.exam_id = ? " +
                     "ORDER BY s.usn";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, examId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(new Student(
                        rs.getString("usn"),
                        rs.getString("name"),
                        rs.getString("branch"),
                        rs.getInt("semester"),
                        rs.getString("section")
                    ));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error loading registered students: " + e.getMessage());
        }

        return students;
    }
}
