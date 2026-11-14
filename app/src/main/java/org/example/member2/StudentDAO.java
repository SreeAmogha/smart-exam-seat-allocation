
package org.example.member2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.example.member1.Student;

public class StudentDAO {

    private Connection getConnection() throws SQLException {

        String url = System.getenv("SUPABASE_DB_URL");
        String user = System.getenv("SUPABASE_DB_USER");
        String password = System.getenv("SUPABASE_DB_PASSWORD");

        if (url == null || user == null || password == null) {
            throw new SQLException("Database environment variables are missing.");
        }

        return DriverManager.getConnection(url, user, password);
    }

    // Register student in Supabase
    public boolean insertStudent(Student student) {

        if (student == null || student.getUsn() == null
                || student.getUsn().isBlank()) {
            System.out.println("Invalid student details.");
            return false;
        }

        String query = """
            INSERT INTO students
            (student_id, usn, name, branch, semester, section)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (usn) DO NOTHING
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, student.getUsn());
            pstmt.setString(3, student.getName());
            pstmt.setString(4, student.getBranch());
            pstmt.setInt(5, student.getSemester());
            pstmt.setString(6, student.getSection());

            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Student registered successfully!");
                return true;
            }

            System.out.println("Student with this USN already exists.");
            return false;

        } catch (SQLException e) {
            System.out.println("Student registration failed: " + e.getMessage());
            return false;
        }
    }

public List<Student> getAllStudents() {

    List<Student> students = new ArrayList<>();

    String query = """
        SELECT usn, name, branch, semester, section
        FROM students
        ORDER BY usn
        """;

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            Student student = new Student(
                rs.getString("usn"),
                rs.getString("name"),
                rs.getString("branch"),
                rs.getInt("semester"),
                rs.getString("section")
            );

            students.add(student);
        }

    } catch (SQLException e) {
        System.out.println("Could not retrieve students: " + e.getMessage());
    }

    return students;
}

}

