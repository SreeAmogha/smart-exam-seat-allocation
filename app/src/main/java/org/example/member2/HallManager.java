
package org.example.member2;

import java.util.HashMap;
import java.util.Map;

public class HallManager {

    // Store halls using hall ID as the key
    private Map<String, Hall> halls = new HashMap<>();


    // METHOD 1: Create a hall
    public boolean createHall(String hallId, String hallName,
                              int numberOfRows, int seatsPerRow) {

        if (hallId == null || hallId.trim().isEmpty()
                || hallName == null || hallName.trim().isEmpty()
                || numberOfRows <= 0 || seatsPerRow <= 0) {

            System.out.println("Invalid hall details.");
            return false;
        }

        // Prevent duplicate hall IDs
        if (halls.containsKey(hallId)) {

            System.out.println("Hall ID already exists.");
            return false;
        }

        Hall newHall = new Hall(
            hallId, hallName, numberOfRows, seatsPerRow
        );

        halls.put(hallId, newHall);

        System.out.println("Hall created successfully!");

        return true;
    }


    // METHOD 2: Find a hall
    public Hall findHall(String hallId) {

        return halls.get(hallId);
    }


    // METHOD 3: Display all halls
    public void displayAllHalls() {

        if (halls.isEmpty()) {

            System.out.println("No halls available.");
            return;
        }

        for (Hall hall : halls.values()) {

            System.out.println("--------------------");
            hall.displayHall();
        }
    }


    // METHOD 4: Calculate total capacity
    public int getTotalCapacity() {

        int total = 0;

        for (Hall hall : halls.values()) {

            total += hall.getTotalCapacity();
        }

        return total;
    }


    // METHOD 5: Get number of halls
    public int getHallCount() {

        return halls.size();
    }
}
give
