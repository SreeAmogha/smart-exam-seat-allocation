package org.example.member1;

public class StudentLinkedList {
    
    public static class Node {
        Student student;
        Node next;

        public Node(Student student) {
            this.student = student;
            this.next = null;
        }
    }

    private Node head;

    public StudentLinkedList() {
        this.head = null;
    }

    // Insert student at the end of the list
    public void add(Student student) {
        Node newNode = new Node(student);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
    }

    // Delete student by USN from Linked List
    public boolean delete(String usn) {
        if (head == null) return false;

        if (head.student.getUsn().equalsIgnoreCase(usn)) {
            head = head.next;
            return true;
        }

        Node current = head;
        while (current.next != null) {
            if (current.next.student.getUsn().equalsIgnoreCase(usn)) {
                current.next = current.next.next;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // Search student by USN in Linked List
    public Student search(String usn) {
        Node current = head;
        while (current != null) {
            if (current.student.getUsn().equalsIgnoreCase(usn)) {
                return current.student;
            }
            current = current.next;
        }
        return null;
    }

    public Node getHead() {
        return head;
    }
}
