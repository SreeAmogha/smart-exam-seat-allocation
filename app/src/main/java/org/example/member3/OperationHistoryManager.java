
package org.example.member3;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.models.AllocationHistory;

public class OperationHistoryManager {

    private List<AllocationHistory> historyList;
    private int nextHistoryId;

    public OperationHistoryManager() {
        historyList = new ArrayList<>();
        nextHistoryId = 1;
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

        AllocationHistory history = new AllocationHistory(
                nextHistoryId,
                examId,
                operationType,
                studentUSN1,
                studentUSN2,
                oldSeatId1,
                oldSeatId2,
                newSeatId1,
                newSeatId2,
                LocalDateTime.now());

        historyList.add(history);
        nextHistoryId++;
    }

    public List<AllocationHistory> getAllHistory() {
        return new ArrayList<>(historyList);
    }

    public List<AllocationHistory> getHistoryByExam(String examId) {

        List<AllocationHistory> filteredHistory = new ArrayList<>();

        for (AllocationHistory history : historyList) {
            if (history.getExamId().equals(examId)) {
                filteredHistory.add(history);
            }
        }

        return filteredHistory;
    }

    public List<AllocationHistory> getHistoryByStudent(String usn) {

        List<AllocationHistory> filteredHistory = new ArrayList<>();

        if (usn == null || usn.isBlank()) {
            return filteredHistory;
        }

        for (AllocationHistory history : historyList) {
            if (usn.equalsIgnoreCase(history.getStudentUSN1())
                    || usn.equalsIgnoreCase(history.getStudentUSN2())) {
                filteredHistory.add(history);
            }
        }

        return filteredHistory;
    }
}
