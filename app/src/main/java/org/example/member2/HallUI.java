package org.example.member2;

import java.util.Scanner;

public class HallUI {
    private HallManager hallManager;
    private Scanner scanner;

    public HallUI() {
        this.hallManager = new HallManager();
        this.scanner = new Scanner(System.in);
    }

    public void displaySeatingArrangement(Hall hall) {
        if (hall == null || hall.getRows() == null) {
            System.out.println("Hall or seating layout is empty.");
            return;
        }

        System.out.println("\n========================================");
        System.out.println(" SEATING ARRANGEMENT: " + hall.getHallName() + " (ID: " + hall.getHallId() + ")");
        System.out.println("========================================");

        RowNode currentRow = hall.getRows().getHead();
        int rowNum = 1;
        while (currentRow != null) {
            System.out.print("Row " + rowNum + " -> ");
            DoublyLinkedSeatList seatList = currentRow.getSeatList();
            SeatNode currentSeat = seatList.getHead();
            
            while (currentSeat != null) {
                String usn = currentSeat.getAllocatedUsn();
                if (usn == null || usn.isEmpty()) {
                    System.out.print("[ EMPTY ] ");
                } else {
                    System.out.print("[" + usn + "] ");
                }
                currentSeat = currentSeat.getNext();
            }
            System.out.println();
            currentRow = currentRow.getNext();
            rowNum++;
        }
        System.out.println("========================================\n");
    }

    public void showHallMenu() {
        System.out.println("--- Member 2: Hall & Allocation Management ---");
        System.out.println("1. View Hall Seating");
        System.out.println("2. Exit");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        
        if (choice == 1) {
            System.out.print("Enter Hall ID to view: ");
            String hallId = scanner.next();
            Hall hall = hallManager.getHall(hallId);
            if (hall != null) {
                displaySeatingArrangement(hall);
            } else {
                System.out.println("Hall not found!");
            }
        }
    }
}
