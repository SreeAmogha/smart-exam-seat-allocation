
package org.example.member3;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.example.models.AllocationHistory;

public class ReportsUI {

    private ReportGenerator reportGenerator;

    private JFrame frame;

    private JTextField examIdField;
    private JTextField studentUSNField;

    private JLabel totalOperationsLabel;
    private JLabel totalSwapsLabel;
    private JLabel totalReassignmentsLabel;
    private JLabel totalUndoLabel;

    private JTable historyTable;
    private DefaultTableModel tableModel;

    public ReportsUI(ReportGenerator reportGenerator) {
        this.reportGenerator = reportGenerator;
        createWindow();
    }

    private void createWindow() {

        frame = new JFrame("Exam Reports");
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));

        // TITLE
        JLabel titleLabel = new JLabel(
                "Exam Reports and Operation History",
                SwingConstants.CENTER
        );

        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 10, 10)
        );

        frame.add(titleLabel, BorderLayout.NORTH);

        // MAIN PANEL
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 20, 20, 20)
        );

        frame.add(mainPanel, BorderLayout.CENTER);

        // SEARCH PANEL
        JPanel searchPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        searchPanel.setBorder(
                BorderFactory.createTitledBorder("Search Reports")
        );

        searchPanel.add(new JLabel("Exam ID:"));
        examIdField = new JTextField();
        searchPanel.add(examIdField);

        JButton examReportButton = new JButton("View Exam Report");
        searchPanel.add(examReportButton);

        searchPanel.add(new JLabel("Student USN:"));
        studentUSNField = new JTextField();
        searchPanel.add(studentUSNField);

        JButton studentReportButton = new JButton("View Student History");
        searchPanel.add(studentReportButton);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // STATISTICS PANEL
        JPanel statisticsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        statisticsPanel.setBorder(
                BorderFactory.createTitledBorder("Operation Statistics")
        );

        totalOperationsLabel = new JLabel("Total Operations: 0");
        totalSwapsLabel = new JLabel("Total Swaps: 0");
        totalReassignmentsLabel = new JLabel("Total Reassignments: 0");
        totalUndoLabel = new JLabel("Total Undo Operations: 0");

        statisticsPanel.add(totalOperationsLabel);
        statisticsPanel.add(totalSwapsLabel);
        statisticsPanel.add(totalReassignmentsLabel);
        statisticsPanel.add(totalUndoLabel);

        mainPanel.add(statisticsPanel, BorderLayout.CENTER);

        // HISTORY TABLE
        String[] columns = {
                "History ID",
                "Exam ID",
                "Operation",
                "Student 1",
                "Student 2",
                "Old Seat 1",
                "Old Seat 2",
                "New Seat 1",
                "New Seat 2",
                "Performed At"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(
                BorderFactory.createTitledBorder("Operation History")
        );

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        JButton clearButton = new JButton("Clear Display");

        clearButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            resetStatistics();
        });

        bottomPanel.add(clearButton, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // BUTTON ACTIONS
        examReportButton.addActionListener(e -> loadExamReport());

        studentReportButton.addActionListener(e -> loadStudentHistory());

        frame.setVisible(true);
    }

    private void loadExamReport() {

        try {
            int examId = Integer.parseInt(
                    examIdField.getText().trim()
            );

            if (examId <= 0) {
                JOptionPane.showMessageDialog(
                        frame, "Exam ID must be positive."
                );
                return;
            }

            int totalOperations =
                    reportGenerator.getTotalOperations(examId);

            int totalSwaps =
                    reportGenerator.getTotalSwaps(examId);

            int totalReassignments =
                    reportGenerator.getTotalReassignments(examId);

            int totalUndo =
                    reportGenerator.getTotalUndoOperations(examId);

            totalOperationsLabel.setText(
                    "Total Operations: " + totalOperations
            );

            totalSwapsLabel.setText(
                    "Total Swaps: " + totalSwaps
            );

            totalReassignmentsLabel.setText(
                    "Total Reassignments: " + totalReassignments
            );

            totalUndoLabel.setText(
                    "Total Undo Operations: " + totalUndo
            );

            List<AllocationHistory> history =
                    reportGenerator.getExamHistory(examId);

            displayHistory(history);

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    frame, "Please enter a valid Exam ID."
            );
        }
    }

    private void loadStudentHistory() {

        String usn = studentUSNField.getText().trim();

        if (usn.isBlank()) {
            JOptionPane.showMessageDialog(
                    frame, "Please enter a Student USN."
            );
            return;
        }

        List<AllocationHistory> history =
                reportGenerator.getStudentHistory(usn);

        int totalOperations =
                reportGenerator.getTotalOperationsByStudent(usn);

        int totalSwaps =
                reportGenerator.getSwapsByStudent(usn);

        int totalReassignments =
                reportGenerator.getReassignmentsByStudent(usn);

        totalOperationsLabel.setText(
                "Total Operations: " + totalOperations
        );

        totalSwapsLabel.setText(
                "Total Swaps: " + totalSwaps
        );

        totalReassignmentsLabel.setText(
                "Total Reassignments: " + totalReassignments
        );

        totalUndoLabel.setText(
                "Total Undo Operations: Not calculated"
        );

        displayHistory(history);
    }

    private void displayHistory(List<AllocationHistory> history) {

        tableModel.setRowCount(0);

        for (AllocationHistory record : history) {

            Object[] row = {
                    record.getHistoryId(),
                    record.getExamId(),
                    record.getOperationType(),
                    record.getStudentUSN1(),
                    record.getStudentUSN2(),
                    record.getOldSeatId1(),
                    record.getOldSeatId2(),
                    record.getNewSeatId1(),
                    record.getNewSeatId2(),
                    record.getPerformedAt()
            };

            tableModel.addRow(row);
        }
    }

    private void resetStatistics() {

        totalOperationsLabel.setText("Total Operations: 0");
        totalSwapsLabel.setText("Total Swaps: 0");
        totalReassignmentsLabel.setText("Total Reassignments: 0");
        totalUndoLabel.setText("Total Undo Operations: 0");
    }
}
