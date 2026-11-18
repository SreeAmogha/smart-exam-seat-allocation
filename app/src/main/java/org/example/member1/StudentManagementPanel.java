
package org.example.member1;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class StudentManagementPanel extends JPanel {

    private final StudentManager studentManager;
    private JTable studentTable;
    private DefaultTableModel tableModel;

    public StudentManagementPanel(StudentManager studentManager) {
        this.studentManager = studentManager;

        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel title = new JLabel("Student Management");
        title.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 10, 15)
        );
        add(title, BorderLayout.NORTH);

        // Table
        String[] columns = {
            "USN", "Name", "Branch", "Semester", "Section"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(25);
        studentTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        // Buttons
        JButton registerButton = new JButton("Register Student");
        JButton searchButton = new JButton("Search Student");
        JButton modifyButton = new JButton("Modify Student");
        JButton deleteButton = new JButton("Delete Student");
        JButton filterButton = new JButton("Filter Students");
        JButton refreshButton = new JButton("Refresh");

        registerButton.addActionListener(e -> registerStudent());
        searchButton.addActionListener(e -> searchStudent());
        modifyButton.addActionListener(e -> modifyStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        filterButton.addActionListener(e -> filterStudents());
        refreshButton.addActionListener(e -> loadStudents());

        JPanel bottomPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        bottomPanel.add(registerButton);
        bottomPanel.add(searchButton);
        bottomPanel.add(modifyButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(filterButton);
        bottomPanel.add(refreshButton);

        add(bottomPanel, BorderLayout.SOUTH);

        loadStudents();
    }

    // Load all students
    private void loadStudents() {
        tableModel.setRowCount(0);

        List<Student> students =
                studentManager.filterStudents(null, 0);

        displayStudents(students);
    }

    // Display students in the table
    private void displayStudents(List<Student> students) {
        tableModel.setRowCount(0);

        for (Student student : students) {
            tableModel.addRow(new Object[]{
                student.getUsn(),
                student.getName(),
                student.getBranch(),
                student.getSemester(),
                student.getSection()
            });
        }
    }

    // Register student
    private void registerStudent() {
        JTextField usnField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField branchField = new JTextField();
        JTextField semesterField = new JTextField();
        JTextField sectionField = new JTextField();

        Object[] fields = {
            "USN:", usnField,
            "Name:", nameField,
            "Branch:", branchField,
            "Semester:", semesterField,
            "Section:", sectionField
        };

        int result = JOptionPane.showConfirmDialog(
                this, fields, "Register Student",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            if (usnField.getText().trim().isEmpty()
                    || nameField.getText().trim().isEmpty()
                    || branchField.getText().trim().isEmpty()
                    || sectionField.getText().trim().isEmpty()
                    || semesterField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(
                        this, "Please fill in all fields."
                );
                return;
            }

            boolean success = studentManager.registerStudent(
                    usnField.getText().trim(),
                    nameField.getText().trim(),
                    branchField.getText().trim(),
                    Integer.parseInt(semesterField.getText().trim()),
                    sectionField.getText().trim()
            );

            if (success) {
                JOptionPane.showMessageDialog(
                        this, "Student registered successfully!"
                );
                loadStudents();
            } else {
                JOptionPane.showMessageDialog(
                        this, "Registration failed. USN may already exist."
                );
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this, "Semester must be a valid number."
            );
        }
    }

    // Search student using USN
    private void searchStudent() {
        String usn = JOptionPane.showInputDialog(
                this, "Enter student USN:"
        );

        if (usn == null || usn.trim().isEmpty()) {
            return;
        }

        Student student = studentManager.searchStudent(usn.trim());

        if (student != null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Student Details\n\n"
                    + "USN: " + student.getUsn() + "\n"
                    + "Name: " + student.getName() + "\n"
                    + "Branch: " + student.getBranch() + "\n"
                    + "Semester: " + student.getSemester() + "\n"
                    + "Section: " + student.getSection()
            );

            // Select the matching row in the table
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).toString()
                        .equalsIgnoreCase(student.getUsn())) {
                    studentTable.setRowSelectionInterval(i, i);
                    break;
                }
            }

        } else {
            JOptionPane.showMessageDialog(
                    this, "Student not found."
            );
        }
    }

    // Modify selected student
    private void modifyStudent() {
        int selectedRow = studentTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this, "Please select a student to modify."
            );
            return;
        }

        String usn = tableModel.getValueAt(selectedRow, 0).toString();
        Student student = studentManager.searchStudent(usn);

        if (student == null) {
            JOptionPane.showMessageDialog(
                    this, "Student not found."
            );
            return;
        }

        JTextField nameField = new JTextField(student.getName());
        JTextField branchField = new JTextField(student.getBranch());
        JTextField semesterField =
                new JTextField(String.valueOf(student.getSemester()));
        JTextField sectionField = new JTextField(student.getSection());

        Object[] fields = {
            "USN: " + usn,
            "Name:", nameField,
            "Branch:", branchField,
            "Semester:", semesterField,
            "Section:", sectionField
        };

        int result = JOptionPane.showConfirmDialog(
                this, fields, "Modify Student",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            if (nameField.getText().trim().isEmpty()
                    || branchField.getText().trim().isEmpty()
                    || semesterField.getText().trim().isEmpty()
                    || sectionField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(
                        this, "Please fill in all fields."
                );
                return;
            }

            boolean success = studentManager.modifyStudent(
                    usn,
                    nameField.getText().trim(),
                    branchField.getText().trim(),
                    Integer.parseInt(semesterField.getText().trim()),
                    sectionField.getText().trim()
            );

            if (success) {
                JOptionPane.showMessageDialog(
                        this, "Student updated successfully!"
                );
                loadStudents();
            } else {
                JOptionPane.showMessageDialog(
                        this, "Student modification failed."
                );
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this, "Semester must be a valid number."
            );
        }
    }

    // Delete selected student
    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this, "Please select a student to delete."
            );
            return;
        }

        String usn = tableModel.getValueAt(selectedRow, 0).toString();

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete student " + usn + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            boolean success = studentManager.deleteStudent(usn);

            if (success) {
                JOptionPane.showMessageDialog(
                        this, "Student deleted successfully!"
                );
                loadStudents();
            } else {
                JOptionPane.showMessageDialog(
                        this, "Student deletion failed."
                );
            }
        }
    }

    // Filter students by branch and semester
    private void filterStudents() {
        JTextField branchField = new JTextField();
        JTextField semesterField = new JTextField();

        Object[] fields = {
            "Branch (leave blank for all):", branchField,
            "Semester (enter 0 for all):", semesterField
        };

        int result = JOptionPane.showConfirmDialog(
                this, fields, "Filter Students",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String branch = branchField.getText().trim();

            if (branch.isEmpty()) {
                branch = null;
            }

            String semesterText = semesterField.getText().trim();
            int semester = semesterText.isEmpty()
                    ? 0 : Integer.parseInt(semesterText);

            List<Student> filteredStudents =
                    studentManager.filterStudents(branch, semester);

            displayStudents(filteredStudents);

            JOptionPane.showMessageDialog(
                    this,
                    filteredStudents.size() + " student(s) found."
            );

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this, "Semester must be a valid number."
            );
        }
    }
}
