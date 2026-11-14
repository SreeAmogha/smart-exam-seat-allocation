
package org.example.member2;

public class Hall {

    private String hallId;
    private String hallName;
    private int totalCapacity;
    private RowLinkedList rows;

    public Hall(String hallId, String hallName,
                int totalCapacity, RowLinkedList rows) {

        this.hallId = hallId;
        this.hallName = hallName;
        this.totalCapacity = totalCapacity;
        this.rows = rows;
    }

    public String getHallId() {
        return hallId;
    }

    public String getHallName() {
        return hallName;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public RowLinkedList getRows() {
        return rows;
    }

    // Display hall details and seating layout
    public void displayHall() {

        System.out.println("Hall ID: " + hallId);
        System.out.println("Hall Name: " + hallName);
        System.out.println("Total Capacity: " + totalCapacity);

        System.out.println("Seating Layout:");

        if (rows != null) {
            rows.displayRows();
        }
    }
}
