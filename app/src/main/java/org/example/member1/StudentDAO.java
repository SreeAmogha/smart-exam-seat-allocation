package org.example.member1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    // Replace with your actual Supabase PostgreSQL connection details
    private static final String URL = "jdbc:postgresql://db.YOUR_PROJECT_REF.supabase.co:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "YOUR_SUPABASE_PASSWORD";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public boolean insertStudent(Student student) {
        String query = "INSERT INTO students (usn, name, branch, semester, section) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, student.getUsn());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getBranch());
            pstmt.setInt(4, student.getSemester());
            pstmt.setString(5, student.getSection());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStudent(String usn, Student updatedStudent) {
        String query = "UPDATE students SET name = ?, branch = ?, semester = ?, section = ? WHERE usn = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, updatedStudent.getName());
            pstmt.setString(2, updatedStudent.getBranch());
            pstmt.setInt(3, updatedStudent.getSemester());
            pstmt.setString(4, updatedStudent.getSection());
            pstmt.setString(5, usn);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStudent(String usn) {
        String query = "DELETE FROM students WHERE usn = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, usn);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String query = "SELECT * FROM students";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Student(
                    rs.getString("usn"),
                    rs.getString("name"),
                    rs.getString("branch"),
                    rs.getInt("semester"),
                    rs.getString("section")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
