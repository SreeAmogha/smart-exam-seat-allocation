
package org.example.models;

public class Operation {

    private String operationType;
    private String examId;
    private String studentUSN1;
    private String studentUSN2;

    private String oldSeatId1;
    private String oldSeatId2;

    private String newSeatId1;
    private String newSeatId2;

    public Operation(
            String examId,
            String operationType,
            String studentUSN1,
            String studentUSN2,
            String oldSeatId1,
            String oldSeatId2,
            String newSeatId1,
            String newSeatId2) {

        this.examId = examId;
        this.operationType = operationType;
        this.studentUSN1 = studentUSN1;
        this.studentUSN2 = studentUSN2;
        this.oldSeatId1 = oldSeatId1;
        this.oldSeatId2 = oldSeatId2;
        this.newSeatId1 = newSeatId1;
        this.newSeatId2 = newSeatId2;
    }

    public String getOperationType() {
        return operationType;
    }

    public String getExamId() {
        return examId;
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
}
