package org.example.models;

public class Allocation {
    private String allocationId;
    private String registrationId;
    private String seatId;
    private String examId;
    private String status;

    public Allocation(String allocationId, String registrationId, String seatId, String examId, String status) {
        this.allocationId = allocationId;
        this.registrationId = registrationId;
        this.seatId = seatId;
        this.examId = examId;
        this.status = status;
    }

    public String getAllocationId() { return allocationId; }
    public void setAllocationId(String allocationId) { this.allocationId = allocationId; }

    public String getRegistrationId() { return registrationId; }
    public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }

    public String getSeatId() { return seatId; }
    public void setSeatId(String seatId) { this.seatId = seatId; }

    public String getExamId() { return examId; }
    public void setExamId(String examId) { this.examId = examId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
