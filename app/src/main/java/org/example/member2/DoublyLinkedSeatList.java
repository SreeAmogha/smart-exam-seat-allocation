
package org.example.member2;

public class DoublyLinkedSeatList {

    // First seat in the list
    SeatNode head;

    // Last seat in the list
    SeatNode tail;

    // Number of seats
    int size = 0;


    // METHOD 1: Add a seat
    public void addSeat(int rowNumber, int seatNumber) {

        SeatNode newSeat =
            new SeatNode(rowNumber, seatNumber);

        // If the list is empty
        if (head == null) {

            head = newSeat;
            tail = newSeat;

        } else {

            // Connect the new seat
            tail.next = newSeat;
            newSeat.prev = tail;

            // Update tail
            tail = newSeat;
        }

        size++;
    }


    // METHOD 2: Display from first to last
    public void displayForward() {

        SeatNode current = head;

        while (current != null) {

            System.out.print(current.seatNumber + " ");

            current = current.next;
        }

        System.out.println();
    }


    // METHOD 3: Display from last to first
    public void displayBackward() {

        SeatNode current = tail;

        while (current != null) {

            System.out.print(current.seatNumber + " ");

            current = current.prev;
        }

        System.out.println();
    }


    // METHOD 4: Return total seats
    public int getSize() {

        return size;
    }
}
