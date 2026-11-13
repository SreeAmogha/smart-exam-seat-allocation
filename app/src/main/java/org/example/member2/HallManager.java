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
        hallMap.put(hall.getHallId(), hall);
        hallDAO.insertHall(hall.getHallId(), hall.getHallName(), hall.getTotalCapacity());
    }

    public Hall getHall(String hallId) {
        return hallMap.get(hallId);
    }
}
