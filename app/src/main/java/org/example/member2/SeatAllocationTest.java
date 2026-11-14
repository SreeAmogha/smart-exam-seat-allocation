
package org.example.member2;

import java.util.ArrayList;
import java.util.List;

import org.example.member1.Student;

public class SeatAllocationTest {

    public static void main(String[] args) {

        // Create a hall with 2 rows and 3 seats per row.
        RowLinkedList rows = new RowLinkedList();

        rows.addRow(1, 3);
        rows.addRow(2, 3);

        Hall hall = new Hall("H01", "Test Hall", 6, rows);

        // Create sample students.
        List<Student> students = new ArrayList<>();

students.add(new Student("S001", "Asha", "CSE", 3, "A"));
students.add(new Student("S002", "Ravi", "ECE", 3, "A"));
students.add(new Student("S003", "Meera", "ME", 3, "A"));
students.add(new Student("S004", "Arun", "CSE", 3, "A"));

        SeatAllocator allocator = new SeatAllocator();

        boolean success = allocator.allocateHall(hall, students);

        System.out.println("ALLOCATION RESULT");

        HallUI ui = new HallUI();
        ui.displaySeatingArrangement(hall);

        System.out.println("All students allocated: " + success);

        System.out.println("\nUNALLOCATED STUDENTS:");

        for (Student student : allocator.getUnallocatedStudents()) {
            System.out.println(student);
        }
    }
}
