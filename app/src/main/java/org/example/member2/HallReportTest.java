
package org.example.member2;

public class HallReportTest {

    public static void main(String[] args) {

        HallManager manager = new HallManager();

        manager.createHall("H01", "Hall A", 2, 3);
        manager.createHall("H02", "Hall B", 2, 4);

        Hall hall1 = manager.getHall("H01");
        Hall hall2 = manager.getHall("H02");

        // Allocate 2 students in Hall A
        SeatNode seat = hall1.getRows().getHead()
                .getSeatList().getHead();

        seat.setAssignedUsn("STUDENT001");
        seat.getNext().setAssignedUsn("STUDENT002");

        // Allocate 3 students in Hall B
        seat = hall2.getRows().getHead()
                .getSeatList().getHead();

        seat.setAssignedUsn("STUDENT003");
        seat.getNext().setAssignedUsn("STUDENT004");
        seat.getNext().getNext().setAssignedUsn("STUDENT005");

        HallReport report = new HallReport();
        report.displayAllReports(manager);
    }
}
