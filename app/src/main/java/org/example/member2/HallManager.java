
package org.example.member2;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HallManager {

    private Map<String, Hall> hallMap;
    private HallDAO hallDAO;

    public HallManager() {
        this.hallMap = new HashMap<>();
        this.hallDAO = new HallDAO();
    }

    // Add an existing hall
    public void addHall(Hall hall) {

        if (hall == null || hall.getHallId() == null
                || hallMap.containsKey(hall.getHallId())) {
            System.out.println("Invalid hall or duplicate Hall ID.");
            return;
        }

        hallMap.put(hall.getHallId(), hall);
    }

    // Get hall using its ID
    public Hall getHall(String hallId) {
        return hallMap.get(hallId);
    }

    // Create a hall
    public boolean createHall(String hallId, String hallName,
                              int numberOfRows, int seatsPerRow) {

        if (hallId == null || hallId.trim().isEmpty()
                || hallName == null || hallName.trim().isEmpty()
                || numberOfRows <= 0 || seatsPerRow <= 0) {

            System.out.println("Invalid hall details.");
            return false;
        }

        if (hallMap.containsKey(hallId)) {
            System.out.println("Hall ID already exists.");
            return false;
        }

        Hall newHall = new Hall(
                hallId, hallName, numberOfRows, seatsPerRow
        );

        hallMap.put(hallId, newHall);

        System.out.println("Hall created successfully!");
        return true;
    }

    // Find hall
    public Hall findHall(String hallId) {
        return getHall(hallId);
    }

    // Display all halls
    public void displayAllHalls() {

        if (hallMap.isEmpty()) {
            System.out.println("No halls available.");
            return;
        }

        for (Hall hall : hallMap.values()) {
            System.out.println("--------------------");
            hall.displayHall();
        }
    }

    // Get number of halls
    public int getHallCount() {
        return hallMap.size();
    }

    // Calculate total capacity
    public int getTotalCapacity() {

        int total = 0;

        for (Hall hall : hallMap.values()) {
            total += hall.getTotalCapacity();
        }

        return total;
    }
    public Collection<Hall> getAllHalls() {
    return hallMap.values();
}
}
