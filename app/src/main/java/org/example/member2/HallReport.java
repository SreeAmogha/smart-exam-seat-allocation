
package org.example.member2;

public class HallReport {

    public void displayAllReports(HallManager manager) {

        if (manager == null || manager.getHallCount() == 0) {
            System.out.println("No halls available for reporting.");
            return;
        }

        int overallCapacity = 0;
        int overallOccupied = 0;

        System.out.println("\n========== HALL-WISE REPORTS ==========");

        for (Hall hall : manager.getAllHalls()) {

            int occupied = 0;
            int vacant = 0;

            RowNode currentRow = hall.getRows().getHead();

            while (currentRow != null) {

                SeatNode currentSeat =
                        currentRow.getSeatList().getHead();

                while (currentSeat != null) {

                    if (currentSeat.isOccupied()) {
                        occupied++;
                    } else {
                        vacant++;
                    }

                    currentSeat = currentSeat.getNext();
                }

                currentRow = currentRow.getNext();
            }

            int total = occupied + vacant;

            double percentage = total == 0
                    ? 0
                    : (occupied * 100.0) / total;

            System.out.println("\nHall ID: " + hall.getHallId());
            System.out.println("Hall Name: " + hall.getHallName());
            System.out.println("Total Seats: " + total);
            System.out.println("Occupied Seats: " + occupied);
            System.out.println("Vacant Seats: " + vacant);
            System.out.printf("Occupancy: %.2f%%%n", percentage);

            System.out.println("---------------------------------------");

            overallCapacity += total;
            overallOccupied += occupied;
        }

        double overallPercentage = overallCapacity == 0
                ? 0
                : (overallOccupied * 100.0) / overallCapacity;

        System.out.println("\n========== OVERALL SUMMARY ==========");
        System.out.println("Total Seats: " + overallCapacity);
        System.out.println("Occupied Seats: " + overallOccupied);
        System.out.println("Vacant Seats: "
                + (overallCapacity - overallOccupied));
        System.out.printf("Overall Occupancy: %.2f%%%n",
                overallPercentage);
    }
}
