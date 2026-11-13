package org.example.member2;

public class Seat {
    private int seatNumber;
    private boolean isOccupied;
    private String allocatedUsn;

    public Seat(int seatNumber) {
        this.seatNumber = seatNumber;
        this.isOccupied = false;
        this.allocatedUsn = null;
    }

    public int getSeatNumber() { return seatNumber; }
    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { isOccupied = occupied; }
    
    public String getAllocatedUsn() { return allocatedUsn; }
    public void setAllocatedUsn(String allocatedUsn) { 
        this.allocatedUsn = allocatedUsn;
        this.isOccupied = (allocatedUsn != null && !allocatedUsn.isEmpty());
    }
}
