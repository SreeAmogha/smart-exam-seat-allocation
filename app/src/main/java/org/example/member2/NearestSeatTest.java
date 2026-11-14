
package org.example.member2;

public class NearestSeatTest {

    public static void main(String[] args) {

        RowLinkedList rows = new RowLinkedList();

        rows.addRow(1, 3);
        rows.addRow(2, 3);
        rows.addRow(3, 3);

        Hall hall = new Hall("H01", "Test Hall", 9, rows);

        // Occupy a few seats.
        rows.getHead().getSeatList().getHead().setAssignedUsn("S001");

        rows.getHead().getNext()
            .getSeatList().getHead()
            .setAssignedUsn("S002");

        // Reference seat: Row 1, Seat 2.
        int referenceRow = 1;
int referenceSeat = 1;

        NearestSeatFinder finder = new NearestSeatFinder();

        SeatNode nearest = finder.findNearestVacantSeat(
            hall, referenceRow, referenceSeat
        );

        System.out.println("REFERENCE SEAT: Row "
            + referenceRow + ", Seat " + referenceSeat);

        if (nearest != null) {
            System.out.println("NEAREST VACANT SEAT: Row "
                + nearest.getRowNumber()
                + ", Seat " + nearest.getSeatNumber());

            int distance =
                Math.abs(referenceRow - nearest.getRowNumber())
                + Math.abs(referenceSeat - nearest.getSeatNumber());

            System.out.println("MANHATTAN DISTANCE: " + distance);
        } else {
            System.out.println("No vacant seats available.");
        }
    }
}
