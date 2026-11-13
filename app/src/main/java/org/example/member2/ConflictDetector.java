package org.example.member2;

import org.example.member1.Student;
import java.util.*;

public class ConflictDetector {
    private Map<String, List<String>> adjacencyList = new HashMap<>();

    public void addStudentNode(String usn) {
        adjacencyList.putIfAbsent(usn, new ArrayList<>());
    }

    public boolean hasConflict(Student student1, Student student2) {
        if (student1 == null || student2 == null) return false;
        // Check if adjacent students share the same branch/subject
        return student1.getBranch().equalsIgnoreCase(student2.getBranch());
    }
}
