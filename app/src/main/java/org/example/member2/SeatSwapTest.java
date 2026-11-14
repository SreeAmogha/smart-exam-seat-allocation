
package org.example.member2;

public class SeatSwapTest {

    public static void main(String[] args) {

        // Create a hall with 2 rows and 3 seats per row
        Hall hall = new Hall("H01", "Test Hall", 2, 3);

        // Assign students to two seats
        SeatNode seat1 = hall.getRows().getHead()
                .getSeatList().getHead();

        SeatNode seat2 = seat1.getNext();

        seat1.setAssignedUsn("1RV23CS001");
        seat2.setAssignedUsn("1RV23CS002");

        System.out.println("BEFORE SWAP");
        System.out.println("Seat 1: " + seat1.getAllocatedUsn());
        System.out.println("Seat 2: " + seat2.getAllocatedUsn());

        // Perform the swap
        SeatSwapManager swapManager = new SeatSwapManager();

        boolean result = swapManager.swapSeats(
                hall, 1, 1, 1, 2
        );

        System.out.println("\nAFTER SWAP");
        System.out.println("Seat 1: " + seat1.getAllocatedUsn());
        System.out.println("Seat 2: " + seat2.getAllocatedUsn());

        System.out.println("\nSwap successful: " + result);
    }
}
