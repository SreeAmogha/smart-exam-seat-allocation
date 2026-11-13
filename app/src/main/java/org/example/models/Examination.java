package org.example.models;

public class Examination {
    private String examId;
    private String examName;
    private String examDate;
    private String session;

    public Examination(String examId, String examName, String examDate, String session) {
        this.examId = examId;
        this.examName = examName;
        this.examDate = examDate;
        this.session = session;
    }

    public String getExamId() { return examId; }
    public void setExamId(String examId) { this.examId = examId; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getExamDate() { return examDate; }
    public void setExamDate(String examDate) { this.examDate = examDate; }

    public String getSession() { return session; }
    public void setSession(String session) { this.session = session; }
}
