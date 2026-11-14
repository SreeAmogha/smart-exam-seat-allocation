package org.example.member2;

import java.util.HashMap;
import java.util.Map;

public class HallManager {
    private Map<String, Hall> hallMap;
    private HallDAO hallDAO;

    public HallManager() {
        this.hallMap = new HashMap<>();
        this.hallDAO = new HallDAO();
    }

    public void addHall(Hall hall) {
    if (hall == null) {
        return;
    }

    hallMap.put(hall.getHallId(), hall);
}

    public Hall getHall(String hallId) {
        return hallMap.get(hallId);
    }

    // Creates a hall with the given number of rows and seats per row.
    public void createHall(
        String hallId,
        String hallName,
        int numberOfRows,
        int seatsPerRow
    ) {
        if (numberOfRows <= 0 || seatsPerRow <= 0) {
            System.out.println("Rows and seats per row must be greater than zero.");
            return;
        }

        RowLinkedList rows = new RowLinkedList();

        for (int i = 1; i <= numberOfRows; i++) {
            rows.addRow(i, seatsPerRow);
        }

        int capacity = rows.getTotalCapacity();

        Hall hall = new Hall(hallId, hallName, capacity, rows);
        addHall(hall);
    }

    public void displayAllHalls() {
        if (hallMap.isEmpty()) {
            System.out.println("No halls available.");
            return;
        }

        for (Hall hall : hallMap.values()) {
            hall.displayHall();
            System.out.println("--------------------");
        }
    }

    public int getHallCount() {
        return hallMap.size();
    }

    public int getTotalCapacity() {
        int total = 0;

        for (Hall hall : hallMap.values()) {
            total += hall.getTotalCapacity();
        }

        return total;
    }

    public Hall findHall(String hallId) {
        return getHall(hallId);
    }
}