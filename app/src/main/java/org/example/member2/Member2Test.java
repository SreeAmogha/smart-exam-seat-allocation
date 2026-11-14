package org.example.member2;

public class Member2Test {
    public static void main(String[] args) {

        HallManager manager = new HallManager();

        manager.createHall("H01", "Hall A", 3, 10);
        manager.createHall("H02", "Hall B", 4, 10);
        manager.createHall("H03", "Hall C", 5, 8);

        System.out.println("\nALL EXAMINATION HALLS");
        manager.displayAllHalls();

        System.out.println("\nTotal Halls: " + manager.getHallCount());
        System.out.println("Total Capacity: " + manager.getTotalCapacity());

        System.out.println("\nSEARCHING FOR H02");

        Hall hall = manager.findHall("H02");

        if (hall != null) {
            hall.displayHall();
        } else {
            System.out.println("Hall not found.");
        }
    }
}