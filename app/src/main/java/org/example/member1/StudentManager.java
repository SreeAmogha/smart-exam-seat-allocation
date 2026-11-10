package org.example.member1;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class StudentManager {
    private HashMap<String, Student> studentMap;
    private StudentLinkedList studentList;
    private StudentDAO studentDAO;

    public StudentManager() {
        this.studentMap = new HashMap<>();
        this.studentList = new StudentLinkedList();
        this.studentDAO = new StudentDAO();
        loadDataFromDatabase();
    }

    // Load records from Supabase into memory structures
    private void loadDataFromDatabase() {
        List<Student> dbStudents = studentDAO.getAllStudents();
        for (Student s : dbStudents) {
            studentMap.put(s.getUsn().toUpperCase(), s);
            studentList.add(s);
        }
    }

    // Student Registration with Duplicate USN Prevention ($O(1)$ lookup)
    public boolean registerStudent(String usn, String name, String branch, int semester, String section) {
        String key = usn.toUpperCase();
        if (studentMap.containsKey(key)) {
            System.out.println("Error: Student with USN " + usn + " already exists!");
            return false;
        }

        Student newStudent = new Student(usn, name, branch, semester, section);
        boolean saved = studentDAO.insertStudent(newStudent);
        
        if (saved) {
            studentMap.put(key, newStudent);
            studentList.add(newStudent);
            System.out.println("Student registered successfully!");
            return true;
        }
        return false;
    }

    // Search using USN via HashMap ($O(1)$)
    public Student searchStudent(String usn) {
        return studentMap.get(usn.toUpperCase());
    }

    // Modify Student Details
    public boolean modifyStudent(String usn, String name, String branch, int semester, String section) {
        String key = usn.toUpperCase();
        if (!studentMap.containsKey(key)) {
            System.out.println("Error: Student not found.");
            return false;
        }

        Student updated = new Student(usn, name, branch, semester, section);
        boolean updatedInDb = studentDAO.updateStudent(usn, updated);
        
        if (updatedInDb) {
            studentMap.put(key, updated);
            refreshLinkedList();
            System.out.println("Student details updated successfully!");
            return true;
        }
        return false;
    }

    // Delete Student
    public boolean deleteStudent(String usn) {
        String key = usn.toUpperCase();
        if (!studentMap.containsKey(key)) {
            System.out.println("Error: Student not found.");
            return false;
        }

        boolean deletedFromDb = studentDAO.deleteStudent(usn);
        if (deletedFromDb) {
            studentMap.remove(key);
            studentList.delete(usn);
            System.out.println("Student deleted successfully!");
            return true;
        }
        return false;
    }

    // Filter Students by Branch and/or Semester
    public List<Student> filterStudents(String branch, int semester) {
        return studentMap.values().stream()
                .filter(s -> (branch == null || s.getBranch().equalsIgnoreCase(branch)) &&
                             (semester == 0 || s.getSemester() == semester))
                .collect(Collectors.toList());
    }

    private void refreshLinkedList() {
        studentList = new StudentLinkedList();
        for (Student s : studentMap.values()) {
            studentList.add(s);
        }
    }
}
