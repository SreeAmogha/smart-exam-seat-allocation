
package org.example.member2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.example.member1.Student;

public class MultiHallAllocationTest {

    public static void main(String[] args) {

        // Create two examination halls.
        RowLinkedList rows1 = new RowLinkedList();
        rows1.addRow(1, 3);
        rows1.addRow(2, 3);

        RowLinkedList rows2 = new RowLinkedList();
        rows2.addRow(1, 3);
        rows2.addRow(2, 3);

        Hall hall1 = new Hall("H01", "Hall A", 6, rows1);
        Hall hall2 = new Hall("H02", "Hall B", 6, rows2);

        List<Hall> halls = Arrays.asList(hall1, hall2);

        // Create students.
        List<Student> students = new ArrayList<>();

        students.add(new Student("S001", "Asha", "CSE", 3, "A"));
        students.add(new Student("S002", "Ravi", "ECE", 3, "A"));
        students.add(new Student("S003", "Meera", "CSE", 3, "A"));
        students.add(new Student("S004", "Arun", "ME", 3, "A"));
        students.add(new Student("S005", "Neha", "ECE", 3, "A"));
        students.add(new Student("S006", "Kiran", "CSE", 3, "A"));
        students.add(new Student("S007", "John", "CSE", 3, "A"));

        SeatAllocator allocator = new SeatAllocator();

        List<Student> remaining =
            allocator.allocateAcrossHalls(halls, students);

        // Display both seating arrangements.
        HallUI ui = new HallUI();

        System.out.println("HALL 1 RESULT");
        ui.displaySeatingArrangement(hall1);

        System.out.println("HALL 2 RESULT");
        ui.displaySeatingArrangement(hall2);

        System.out.println("UNALLOCATED STUDENTS:");

        for (Student student : remaining) {
            System.out.println(student);
        }

        System.out.println(
            "All students allocated: " + remaining.isEmpty()
        );
    }
}
