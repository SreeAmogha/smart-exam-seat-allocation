
package org.example.member2;

public class Hall {

    private String hallId;
    private String hallName;

    private int numberOfRows;
    private int seatsPerRow;

    private RowLinkedList rows;

    // Constructor
    public Hall(String hallId, String hallName,
                int numberOfRows, int seatsPerRow) {

        this.hallId = hallId;
        this.hallName = hallName;
        this.numberOfRows = numberOfRows;
        this.seatsPerRow = seatsPerRow;

        // Create the row linked list
        rows = new RowLinkedList();

        // Automatically create all rows
        for (int i = 1; i <= numberOfRows; i++) {
            rows.addRow(i, seatsPerRow);
        }
    }

    public String getHallId() {
        return hallId;
    }

    public String getHallName() {
        return hallName;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public int getSeatsPerRow() {
        return seatsPerRow;
    }

    public int getTotalCapacity() {
        return rows.getTotalCapacity();
    }

    public RowLinkedList getRows() {
        return rows;
    }

    public void displayHall() {

        System.out.println("Hall ID: " + hallId);
        System.out.println("Hall Name: " + hallName);
        System.out.println("Number of Rows: " + numberOfRows);
        System.out.println("Seats Per Row: " + seatsPerRow);
        System.out.println("Total Capacity: " + getTotalCapacity());

        System.out.println("Seating Layout:");
        rows.displayRows();
    }
}
