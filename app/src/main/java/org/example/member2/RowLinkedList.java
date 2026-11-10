
package org.example.member2;

public class RowLinkedList {

    // First row
    RowNode head;

    // Last row
    RowNode tail;

    // Number of rows
    int size = 0;

    // Fixed number of seats in every row
    int seatsPerRow = -1;


    // METHOD 1: Add a row
    public void addRow(int rowNumber, int numberOfSeats) {

        // Set the seat count when adding the first row
        if (seatsPerRow == -1) {
            seatsPerRow = numberOfSeats;
        }

        // Ensure every row has the same number of seats
        if (numberOfSeats != seatsPerRow) {
            System.out.println(
                "Error: Every row must have the same number of seats."
            );
            return;
        }

        // Create a new row
        RowNode newRow =
            new RowNode(rowNumber, seatsPerRow);

        // If the list is empty
        if (head == null) {

            head = newRow;
            tail = newRow;

        } else {

            // Connect the new row
            tail.next = newRow;
            newRow.prev = tail;

            // Update tail
            tail = newRow;
        }

        size++;
    }


    // METHOD 2: Display all rows
    public void displayRows() {

        RowNode current = head;

        while (current != null) {

            System.out.print(
                "Row " + current.rowNumber + ": "
            );

            current.seats.displayForward();

            current = current.next;
        }
    }


    // METHOD 3: Display rows backwards
    public void displayRowsBackward() {

        RowNode current = tail;

        while (current != null) {

            System.out.println(
                "Row " + current.rowNumber
            );

            current = current.prev;
        }
    }


    // METHOD 4: Calculate total capacity
    public int getTotalCapacity() {

        RowNode current = head;

        int capacity = 0;

        while (current != null) {

            capacity += current.seats.getSize();

            current = current.next;
        }

        return capacity;
    }


    // METHOD 5: Get number of rows
    public int getRowCount() {

        return size;
    }
}
