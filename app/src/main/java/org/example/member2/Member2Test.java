
package org.example.member2;

public class Member2Test {

    public static void main(String[] args) {

        // Create a hall's row list
        RowLinkedList hall = new RowLinkedList();

        // Add three rows, four seats each
        hall.addRow(1, 4);
        hall.addRow(2, 4);
        hall.addRow(3, 4);

        System.out.println("EXAM HALL SEATING LAYOUT");

        // Display seating layout
        hall.displayRows();

        // Display total rows
        System.out.println(
            "Total rows: " + hall.getRowCount()
        );

        // Display total seats
        System.out.println(
            "Total capacity: " + hall.getTotalCapacity()
        );

        // Display rows backwards
        System.out.println("Rows in reverse:");

        hall.displayRowsBackward();
    }
}
