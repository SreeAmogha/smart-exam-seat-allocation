package org.example.member2;

public class Hall {
    private String hallId;
    private String hallName;
    private int totalCapacity;
    private RowLinkedList rows; // Using your existing RowLinkedList

    public Hall(String hallId, String hallName, int totalCapacity, RowLinkedList rows) {
        this.hallId = hallId;
        this.hallName = hallName;
        this.totalCapacity = totalCapacity;
        this.rows = rows;
    }

    public String getHallId() { return hallId; }
    public String getHallName() { return hallName; }
    public int getTotalCapacity() { return totalCapacity; }
    public RowLinkedList getRows() { return rows; }
}
