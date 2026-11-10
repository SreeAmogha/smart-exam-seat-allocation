
package org.example.member2;

public class SeatNode {

    int seatNumber;
    int rowNumber;

    boolean occupied;

    SeatNode prev;
    SeatNode next;

    public SeatNode(int rowNumber, int seatNumber) {

        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;

        this.occupied = false;

        this.prev = null;
        this.next = null;
    }
}
