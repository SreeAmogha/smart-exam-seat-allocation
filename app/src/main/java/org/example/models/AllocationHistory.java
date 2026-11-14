
package org.example.models;

import java.time.LocalDateTime;

public class AllocationHistory {

    private int historyId;
    private String examId;
    private String operationType;
    private String studentUSN1;
    private String studentUSN2;

    private String oldSeatId1;
    private String oldSeatId2;
    private String newSeatId1;
    private String newSeatId2;

    private LocalDateTime performedAt;

    public AllocationHistory(
            int historyId,
            String examId,
            String operationType,
            String studentUSN1,
            String studentUSN2,
            String oldSeatId1,
            String oldSeatId2,
            String newSeatId1,
            String newSeatId2,
            LocalDateTime performedAt) {

        this.historyId = historyId;
        this.examId = examId;
        this.operationType = operationType;
        this.studentUSN1 = studentUSN1;
        this.studentUSN2 = studentUSN2;
        this.oldSeatId1 = oldSeatId1;
        this.oldSeatId2 = oldSeatId2;
        this.newSeatId1 = newSeatId1;
        this.newSeatId2 = newSeatId2;
        this.performedAt = performedAt;
    }

    public int getHistoryId() {
        return historyId;
    }

    public String getExamId() {
        return examId;
    }

    public String getOperationType() {
        return operationType;
    }

    public String getStudentUSN1() {
        return studentUSN1;
    }

    public String getStudentUSN2() {
        return studentUSN2;
    }

    public String getOldSeatId1() {
        return oldSeatId1;
    }

    public String getOldSeatId2() {
        return oldSeatId2;
    }

    public String getNewSeatId1() {
        return newSeatId1;
    }

    public String getNewSeatId2() {
        return newSeatId2;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }
}
