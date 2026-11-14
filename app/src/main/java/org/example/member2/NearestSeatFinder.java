
package org.example.member2;

public class NearestSeatFinder {

    public SeatNode findNearestVacantSeat(
            Hall hall, int referenceRow, int referenceSeat) {

        if (hall == null || hall.getRows() == null) {
            return null;
        }

        SeatNode nearestSeat = null;
        int minimumDistance = Integer.MAX_VALUE;

        RowNode currentRow = hall.getRows().getHead();

        while (currentRow != null) {

            SeatNode currentSeat =
                currentRow.getSeatList().getHead();

            while (currentSeat != null) {

                if (!currentSeat.isOccupied()) {

                    int distance =
                        Math.abs(referenceRow - currentSeat.getRowNumber())
                        + Math.abs(referenceSeat - currentSeat.getSeatNumber());

                    if (distance < minimumDistance) {
                        minimumDistance = distance;
                        nearestSeat = currentSeat;
                    }
                }

                currentSeat = currentSeat.getNext();
            }

            currentRow = currentRow.getNext();
        }

        return nearestSeat;
    }
}
