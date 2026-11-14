
package org.example.models;

public class SeatLocation {

    private String hallId;
    private int rowNumber;
    private int columnNumber;
    private String seatId;

    public SeatLocation(
            String hallId,
            int rowNumber,
            int columnNumber,
            String seatId) {

        this.hallId = hallId;
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
        this.seatId = seatId;
    }

    public String getHallId() {
        return hallId;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public String getSeatId() {
        return seatId;
    }
}
