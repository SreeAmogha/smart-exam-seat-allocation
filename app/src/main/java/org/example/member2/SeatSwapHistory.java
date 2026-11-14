
package org.example.member2;

import java.util.Stack;

public class SeatSwapHistory {

    private static class SwapRecord {
        Hall hall;
        int row1, seat1, row2, seat2;

        SwapRecord(Hall hall, int row1, int seat1,
                   int row2, int seat2) {
            this.hall = hall;
            this.row1 = row1;
            this.seat1 = seat1;
            this.row2 = row2;
            this.seat2 = seat2;
        }
    }

    private Stack<SwapRecord> history = new Stack<>();
    private SeatSwapManager swapManager = new SeatSwapManager();

    public boolean swapAndSave(Hall hall,
                               int row1, int seat1,
                               int row2, int seat2) {

        boolean success = swapManager.swapSeats(
                hall, row1, seat1, row2, seat2);

        if (success) {
            history.push(new SwapRecord(
                    hall, row1, seat1, row2, seat2));
        }

        return success;
    }

    public boolean undoLastSwap() {

        if (history.isEmpty()) {
            System.out.println("No swaps available to undo.");
            return false;
        }

        SwapRecord last = history.pop();

        boolean success = swapManager.swapSeats(
                last.hall,
                last.row1, last.seat1,
                last.row2, last.seat2);

        if (success) {
            System.out.println("Last swap undone successfully!");
            return true;
        }

        history.push(last);
        return false;
    }

    public int getHistorySize() {
        return history.size();
    }
}
