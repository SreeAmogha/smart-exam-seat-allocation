
package org.example.member2;

public class Hall {

    private String hallId;
    private String hallName;
    private int totalCapacity;
    private int numberOfRows;
    private int seatsPerRow;
    private RowLinkedList rows;

    // Constructor using an existing row linked list
    public Hall(String hallId, String hallName,
                int totalCapacity, RowLinkedList rows) {

        this.hallId = hallId;
        this.hallName = hallName;
        this.rows = rows;
        this.totalCapacity = totalCapacity;

        this.numberOfRows = (rows == null) ? 0 : rows.getRowCount();
        this.seatsPerRow = (numberOfRows == 0)
                ? 0 : totalCapacity / numberOfRows;
    }

    // Constructor that automatically creates rows
    public Hall(String hallId, String hallName,
                int numberOfRows, int seatsPerRow) {

        this.hallId = hallId;
        this.hallName = hallName;
        this.numberOfRows = numberOfRows;
        this.seatsPerRow = seatsPerRow;

        rows = new RowLinkedList();

        for (int i = 1; i <= numberOfRows; i++) {
            rows.addRow(i, seatsPerRow);
        }

        this.totalCapacity = rows.getTotalCapacity();
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
        return totalCapacity;
    }

    public RowLinkedList getRows() {
        return rows;
    }

    public void displayHall() {
        System.out.println("Hall ID: " + hallId);
        System.out.println("Hall Name: " + hallName);
        System.out.println("Number of Rows: " + numberOfRows);
        System.out.println("Seats Per Row: " + seatsPerRow);
        System.out.println("Total Capacity: " + totalCapacity);
        System.out.println("Seating Layout:");

        if (rows != null) {
            rows.displayRows();
        }
    }
}
