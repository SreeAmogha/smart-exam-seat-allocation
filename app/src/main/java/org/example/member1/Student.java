package org.example.member1;

public class Student {
    private String usn;
    private String name;
    private String branch;
    private int semester;
    private String section;

    public Student(String usn, String name, String branch, int semester, String section) {
        this.usn = usn;
        this.name = name;
        this.branch = branch;
        this.semester = semester;
        this.section = section;
    }

    // Getters and Setters
    public String getUsn() { return usn; }
    public void setUsn(String usn) { this.usn = usn; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    @Override
    public String toString() {
        return "Student [USN=" + usn + ", Name=" + name + ", Branch=" + branch + 
               ", Semester=" + semester + ", Section=" + section + "]";
    }
}
