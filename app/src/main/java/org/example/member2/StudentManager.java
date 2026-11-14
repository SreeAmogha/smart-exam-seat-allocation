
package org.example.member2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.example.member1.Student;

public class StudentManager {

    private HashMap<String, Student> studentMap;
    private StudentDAO studentDAO;

public StudentManager() {
    studentMap = new HashMap<>();
    studentDAO = new StudentDAO();

    for (Student student : studentDAO.getAllStudents()) {
        studentMap.put(student.getUsn().toUpperCase(), student);
    }

    System.out.println("Loaded " + studentMap.size() + " students.");
}
    // Register a student in the database and HashMap
    public boolean registerStudent(Student student) {

        if (student == null || student.getUsn() == null
                || student.getUsn().isBlank()) {
            System.out.println("Invalid student details.");
            return false;
        }

        String usn = student.getUsn().toUpperCase();

        if (studentMap.containsKey(usn)) {
            System.out.println("Student already registered.");
            return false;
        }

        if (studentDAO.insertStudent(student)) {
            studentMap.put(usn, student);
            return true;
        }

        return false;
    }

    // Search student using USN
    public Student searchStudent(String usn) {
        if (usn == null) {
            return null;
        }

        return studentMap.get(usn.toUpperCase());
    }

    // Return all registered students
    public List<Student> getAllStudents() {
        return new ArrayList<>(studentMap.values());
    }

    // Return total registered students
    public int getStudentCount() {
        return studentMap.size();
    }
}
