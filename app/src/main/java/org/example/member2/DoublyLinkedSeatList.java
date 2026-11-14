
package org.example.member2;

public class DoublyLinkedSeatList {

    // First seat
    SeatNode head;

    // Last seat
    SeatNode tail;

    // Number of seats
    int size = 0;

    // Add a seat
    public void addSeat(int rowNumber, int seatNumber) {

        SeatNode newSeat = new SeatNode(rowNumber, seatNumber);

        if (head == null) {

            head = newSeat;
            tail = newSeat;

        } else {

            tail.next = newSeat;
            newSeat.prev = tail;

            tail = newSeat;
        }

        size++;
    }

    // Get first seat
    public SeatNode getHead() {
        return head;
    }

    // Get last seat
    public SeatNode getTail() {
        return tail;
    }

    // Display seats forward
    public void displayForward() {

        SeatNode current = head;

        while (current != null) {

            System.out.print(current.seatNumber + " ");

            current = current.next;
        }

        System.out.println();
    }

    // Display seats backward
    public void displayBackward() {

        SeatNode current = tail;

        while (current != null) {

            System.out.print(current.seatNumber + " ");

            current = current.prev;
        }

        System.out.println();
    }

    // Get total seats
    public int getSize() {
        return size;
    }
}