

package org.example.member2;

public class SeatSwapManager {

    // Find a seat using its row and seat number
    private SeatNode findSeat(Hall hall, int rowNumber, int seatNumber) {

        if (hall == null || hall.getRows() == null) {
            return null;
        }

        RowNode currentRow = hall.getRows().getHead();

        while (currentRow != null) {

            if (currentRow.getRowNumber() == rowNumber) {

                SeatNode currentSeat = currentRow.getSeatList().getHead();

                while (currentSeat != null) {

                    if (currentSeat.getSeatNumber() == seatNumber) {
                        return currentSeat;
                    }

                    currentSeat = currentSeat.getNext();
                }
            }

            currentRow = currentRow.getNext();
        }

        return null;
    }

    // Swap two seat assignments
    public boolean swapSeats(Hall hall,
                             int row1, int seat1,
                             int row2, int seat2) {

        if (row1 == row2 && seat1 == seat2) {
            System.out.println("Cannot swap a seat with itself.");
            return false;
        }

        SeatNode first = findSeat(hall, row1, seat1);
        SeatNode second = findSeat(hall, row2, seat2);

        if (first == null || second == null) {
            System.out.println("Invalid seat position.");
            return false;
        }

        // Save the first student's USN
        String firstUSN = first.getAllocatedUsn();

        // Exchange the two USNs
        first.setAssignedUsn(second.getAllocatedUsn());
        second.setAssignedUsn(firstUSN);

        System.out.println("Seat swap completed successfully!");
        return true;
    }
}
