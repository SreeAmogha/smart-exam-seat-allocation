package org.example.member1;

public class StudentNode {
    public Student student;
    public StudentNode next;

    public StudentNode(Student student) {
        this.student = student;
        this.next = null;
    }
}
