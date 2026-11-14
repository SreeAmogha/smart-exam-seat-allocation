package org.example.member1;

import java.util.List;

public class ExamRegistration {
    private String usn;
    private List<String> registeredSubjects;

    public ExamRegistration(String usn, List<String> registeredSubjects) {
        this.usn = usn;
        this.registeredSubjects = registeredSubjects;
    }

    public String getUsn() {
        return usn;
    }

    public void setUsn(String usn) {
        this.usn = usn;
    }

    public List<String> getRegisteredSubjects() {
        return registeredSubjects;
    }

    public void setRegisteredSubjects(List<String> registeredSubjects) {
        this.registeredSubjects = registeredSubjects;
    }
}
