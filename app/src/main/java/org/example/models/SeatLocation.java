package org.example.models;
public class SeatLocation{
    private int hallId;
    private int rowNumber;
    private int columnNumber;
    private int seatId;
    public SeatLocation(int hallId,int rowNumber, int columnNumber,int seatId){
        this.hallId=hallId;
        this.rowNumber=rowNumber;
        this.columnNumber=columnNumber;
        this.seatId=seatId;
    }
    public int getHallId(){
        return hallId;
    }
    public int getRowNumber(){
        return rowNumber;
    }
    public int getColumnNumber(){
        return columnNumber;
    }
    public int getSeatId(){
        return seatId;
    }
}