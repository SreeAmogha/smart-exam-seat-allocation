
package org.example.member3;

import java.util.List;

import org.example.models.AllocationHistory;

public class OperationHistoryManager {

    private final OperationHistoryDAO historyDAO;

    public OperationHistoryManager() {
        historyDAO = new OperationHistoryDAO();
    }

    public void addHistory(
            String examId,
            String operationType,
            String studentUSN1,
            String studentUSN2,
            String oldSeatId1,
            String oldSeatId2,
            String newSeatId1,
            String newSeatId2) {

        boolean saved = historyDAO.saveHistory(
                examId,
                operationType,
                studentUSN1,
                studentUSN2,
                oldSeatId1,
                oldSeatId2,
                newSeatId1,
                newSeatId2);

        if (!saved) {
            System.out.println("Operation history could not be saved.");
        }
    }

    public List<AllocationHistory> getAllHistory() {
        return historyDAO.getAllHistory();
    }

    public List<AllocationHistory> getHistoryByExam(String examId) {
        return historyDAO.getHistoryByExam(examId);
    }

    public List<AllocationHistory> getHistoryByStudent(String usn) {
        return historyDAO.getHistoryByStudent(usn);
    }
}
