
package org.example.member2;

public class RowNode {

    // Row number
    int rowNumber;

    // Seats belonging to this row
    DoublyLinkedSeatList seats;

    // Previous and next rows
    RowNode prev;
    RowNode next;

    // Constructor
    public RowNode(int rowNumber, int seatsPerRow) {

        this.rowNumber = rowNumber;

        // Create an empty seat list
        seats = new DoublyLinkedSeatList();

        // Add fixed number of seats
        for (int i = 1; i <= seatsPerRow; i++) {

            seats.addSeat(rowNumber, i);
        }

        prev = null;
        next = null;
    }
}
