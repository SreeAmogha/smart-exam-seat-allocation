package org.example.models;

public class Operation{
    private String operationType;
    private int examId;
    private String studentUSN1;
    private String studentUSN2;

    private int oldSeatId1;
    private int oldSeatId2;

    private int newSeatId1;
    private int newSeatId2;

    public Operation(int examId,String operationType,String studentUSN1,String studentUSN2,int oldSeatId1,int oldSeatId2,int newSeatId1,int newSeatId2){
        this.operationType=operationType;
        this.examId=examId;
        this.studentUSN1=studentUSN1;
        this.studentUSN2=studentUSN2;
        this.oldSeatId1=oldSeatId1;
        this.oldSeatId2=oldSeatId2;
        this.newSeatId1=newSeatId1;
        this.newSeatId2=newSeatId2;
    }

    public String getOperationType() {
        return operationType;
    }

    public int getExamId(){
        return examId;
    }
    public String getStudentUSN1() {
        return studentUSN1;
    }

    public String getStudentUSN2() {
        return studentUSN2;
    }

    public int getOldSeatId1() {
        return oldSeatId1;
    }

    public int getOldSeatId2() {
        return oldSeatId2;
    }

    public int getNewSeatId1() {
        return newSeatId1;
    }

    public int getNewSeatId2() {
        return newSeatId2;
    }
}