package org.example.models;

public class Student {
    private String studentId;
    private String usn;
    private String name;
    private String className;

    public Student(String studentId, String usn, String name, String className) {
        this.studentId = studentId;
        this.usn = usn;
        this.name = name;
        this.className = className;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getUsn() { return usn; }
    public void setUsn(String usn) { this.usn = usn; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
