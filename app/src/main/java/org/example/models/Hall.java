package org.example.models;

public class Hall {
    private String hallId;
    private String hallName;
    private int totalCapacity;

    public Hall(String hallId, String hallName, int totalCapacity) {
        this.hallId = hallId;
        this.hallName = hallName;
        this.totalCapacity = totalCapacity;
    }

    public String getHallId() { return hallId; }
    public void setHallId(String hallId) { this.hallId = hallId; }

    public String getHallName() { return hallName; }
    public void setHallName(String hallName) { this.hallName = hallName; }

    public int getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(int totalCapacity) { this.totalCapacity = totalCapacity; }
}
