package org.example.models;

public class ExamRegistration {
    private String registrationId;
    private String studentId;
    private String examId;
    private String subjectId;

    public ExamRegistration(String registrationId, String studentId, String examId, String subjectId) {
        this.registrationId = registrationId;
        this.studentId = studentId;
        this.examId = examId;
        this.subjectId = subjectId;
    }

    public String getRegistrationId() { return registrationId; }
    public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getExamId() { return examId; }
    public void setExamId(String examId) { this.examId = examId; }

    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
}
