
package org.example.member2;

public class SeatSwapHistoryTest {

    public static void main(String[] args) {

        Hall hall = new Hall("H01", "Test Hall", 2, 3);

        SeatNode seat1 = hall.getRows().getHead()
                .getSeatList().getHead();

        SeatNode seat2 = seat1.getNext();
        SeatNode seat3 = seat2.getNext();

        seat1.setAssignedUsn("STUDENT001");
        seat2.setAssignedUsn("STUDENT002");
        seat3.setAssignedUsn("STUDENT003");

        SeatSwapHistory history = new SeatSwapHistory();

        history.swapAndSave(hall, 1, 1, 1, 2);
        history.swapAndSave(hall, 1, 2, 1, 3);

        System.out.println("\nAFTER TWO SWAPS:");
        System.out.println(seat1.getAllocatedUsn());
        System.out.println(seat2.getAllocatedUsn());
        System.out.println(seat3.getAllocatedUsn());

        history.undoLastSwap();

        System.out.println("\nAFTER ONE UNDO:");
        System.out.println(seat1.getAllocatedUsn());
        System.out.println(seat2.getAllocatedUsn());
        System.out.println(seat3.getAllocatedUsn());

        history.undoLastSwap();

        System.out.println("\nAFTER TWO UNDOS:");
        System.out.println(seat1.getAllocatedUsn());
        System.out.println(seat2.getAllocatedUsn());
        System.out.println(seat3.getAllocatedUsn());

        System.out.println("\nRemaining history: "
                + history.getHistorySize());
    }
}
