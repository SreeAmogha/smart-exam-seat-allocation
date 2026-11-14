
package org.example.member2;

public class SeatNode {

    int seatNumber;
    int rowNumber;

    boolean occupied;

    // USN of the student assigned to this seat
    private String allocatedUsn;

    SeatNode prev;
    SeatNode next;

    public SeatNode(int rowNumber, int seatNumber) {

        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;

        this.occupied = false;
        this.allocatedUsn = null;

        this.prev = null;
        this.next = null;
    }

    // Get seat number
    public int getSeatNumber() {
        return seatNumber;
    }

    // Get row number
    public int getRowNumber() {
        return rowNumber;
    }

    // Check whether seat is occupied
    public boolean isOccupied() {
        return occupied;
    }

    // Get next seat
    public SeatNode getNext() {
        return next;
    }

    // Get previous seat
    public SeatNode getPrev() {
        return prev;
    }

    // Get allocated student USN
    public String getAllocatedUsn() {
        return allocatedUsn;
    }

    // Assign student USN
    public void setAssignedUsn(String usn) {
        this.allocatedUsn = usn;
        this.occupied = (usn != null);
    }
}
