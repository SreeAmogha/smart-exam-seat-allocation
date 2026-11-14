package org.example.member2;

import org.example.member1.Student;

public class ConflictDetector {

    public boolean hasConflict(Student student1, Student student2) {
        if (student1 == null || student2 == null) {
            return false;
        }

        return student1.getBranch().equalsIgnoreCase(
            student2.getBranch()
        );
    }
}