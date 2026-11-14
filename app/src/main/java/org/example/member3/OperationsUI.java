
package org.example.member3;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.example.models.OperationResult;

public class OperationsUI {

    private SeatOperationManager operationManager;

    private JFrame frame;

    private JTextField examIdField;
    private JTextField student1Field;
    private JTextField student2Field;

    private JTextField reassignExamIdField;
    private JTextField usnField;
    private JTextField hallIdField;
    private JTextField seatIdField;

    private JLabel statusLabel;

    public OperationsUI(SeatOperationManager operationManager) {
        this.operationManager = operationManager;
        createWindow();
    }

    private void createWindow() {

        frame = new JFrame("Exam Seat Operations");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel(
                "Seat Operations", SwingConstants.CENTER);

        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 20, 10));

        frame.add(titleLabel, BorderLayout.NORTH);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 1, 15, 15));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 30, 20, 30));

        frame.add(mainPanel, BorderLayout.CENTER);

        // SWAP SEATS PANEL
        JPanel swapPanel = new JPanel();
        swapPanel.setBorder(
                BorderFactory.createTitledBorder("Swap Seats"));
        swapPanel.setLayout(new GridLayout(4, 2, 10, 10));

        swapPanel.add(new JLabel("Exam ID:"));
        examIdField = new JTextField();
        swapPanel.add(examIdField);

        swapPanel.add(new JLabel("Student 1 USN:"));
        student1Field = new JTextField();
        swapPanel.add(student1Field);

        swapPanel.add(new JLabel("Student 2 USN:"));
        student2Field = new JTextField();
        swapPanel.add(student2Field);

        JButton swapButton = new JButton("Swap Seats");

        swapButton.addActionListener(e -> {

            try {
                int examId = Integer.parseInt(
                        examIdField.getText().trim());

                String usn1 = student1Field.getText().trim();
                String usn2 = student2Field.getText().trim();

                OperationResult result =
                        operationManager.swapSeats(examId, usn1, usn2);

                JOptionPane.showMessageDialog(
                        frame, result.getMessage());

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        frame, "Please enter a valid Exam ID.");
            }
        });

        swapPanel.add(new JLabel(""));
        swapPanel.add(swapButton);

        mainPanel.add(swapPanel);

        // REASSIGN SEAT PANEL
        JPanel reassignPanel = new JPanel();
        reassignPanel.setBorder(
                BorderFactory.createTitledBorder("Reassign Seat"));
        reassignPanel.setLayout(new GridLayout(5, 2, 10, 10));

        reassignPanel.add(new JLabel("Exam ID:"));
        reassignExamIdField = new JTextField();
        reassignPanel.add(reassignExamIdField);

        reassignPanel.add(new JLabel("Student USN:"));
        usnField = new JTextField();
        reassignPanel.add(usnField);

        reassignPanel.add(new JLabel("New Hall ID:"));
        hallIdField = new JTextField();
        reassignPanel.add(hallIdField);

        reassignPanel.add(new JLabel("New Seat ID:"));
        seatIdField = new JTextField();
        reassignPanel.add(seatIdField);

        JButton reassignButton = new JButton("Reassign Seat");

        reassignButton.addActionListener(e -> {

            try {
                int examId = Integer.parseInt(
                        reassignExamIdField.getText().trim());

                int hallId = Integer.parseInt(
                        hallIdField.getText().trim());

                int seatId = Integer.parseInt(
                        seatIdField.getText().trim());

                String usn = usnField.getText().trim();

                OperationResult result =
                        operationManager.reassignSeat(
                                examId, usn, hallId, seatId);

                JOptionPane.showMessageDialog(
                        frame, result.getMessage());

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        frame, "Please enter valid numeric IDs.");
            }
        });

        reassignPanel.add(new JLabel(""));
        reassignPanel.add(reassignButton);

        mainPanel.add(reassignPanel);

        // UNDO PANEL
        JPanel undoPanel = new JPanel();
        undoPanel.setBorder(
                BorderFactory.createTitledBorder("Undo Operation"));

        JButton undoButton = new JButton("Undo Last Operation");

        undoButton.addActionListener(e -> {

            boolean success = operationManager.undoLastOperation();

            if (success) {
                JOptionPane.showMessageDialog(
                        frame, "Last operation successfully undone.");
            } else {
                JOptionPane.showMessageDialog(
                        frame, "No operation to undo or undo failed.");
            }
        });

        undoPanel.add(undoButton);
        mainPanel.add(undoPanel);

        // Status label
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(
                BorderFactory.createEmptyBorder(5, 15, 10, 15));

        frame.add(statusLabel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}
