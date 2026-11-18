
package org.example;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.example.member1.StudentManagementPanel;
import org.example.member1.StudentManager;
import org.example.member2.ExamAllocationDAO;
import org.example.member2.SeatAllocationUI;
import org.example.member3.OperationsUI;
import org.example.member3.ReportsUI;

public class MainDashboard extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;

    public MainDashboard(
            StudentManager studentManager,
            SeatAllocationUI seatAllocationUI,
            OperationsUI operationsUI,
            ReportsUI reportsUI) {

        setTitle("Smart Exam Hall Seating and Allocation System");
        setSize(1250, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel(new GridLayout(7, 1, 5, 10));
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 20, 10)
        );
        sidebar.setBackground(new Color(35, 45, 60));

        JLabel heading = new JLabel("EXAM SYSTEM", SwingConstants.CENTER);
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("Arial", Font.BOLD, 18));

        sidebar.add(heading);

        // Main content area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(
                new StudentManagementPanel(studentManager),
                "STUDENTS"
        );

        contentPanel.add(
                createExaminationsPanel(),
                "EXAMINATIONS"
        );

        contentPanel.add(
                seatAllocationUI,
                "SEAT_ALLOCATION"
        );

        contentPanel.add(
                operationsUI,
                "OPERATIONS"
        );

        contentPanel.add(
                reportsUI,
                "REPORTS"
        );

        // Navigation buttons
        addNavigationButton(sidebar, "Students", "STUDENTS");
        addNavigationButton(sidebar, "Examinations", "EXAMINATIONS");
        addNavigationButton(sidebar, "Seat Allocation", "SEAT_ALLOCATION");
        addNavigationButton(sidebar, "Operations", "OPERATIONS");
        addNavigationButton(sidebar, "Reports", "REPORTS");

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, "STUDENTS");
    }

    private void addNavigationButton(
            JPanel sidebar,
            String buttonText,
            String screenName) {

        JButton button = new JButton(buttonText);

        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));

        button.addActionListener(e ->
                cardLayout.show(contentPanel, screenName)
        );

        sidebar.add(button);
    }

    // Temporary examination page
    private JPanel createExaminationsPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));

    JLabel title = new JLabel("Examination Management", SwingConstants.CENTER);
    title.setFont(new Font("Arial", Font.BOLD, 24));

    String[] columns = {"Exam ID", "Exam Name", "Exam Date", "Session"};

    DefaultTableModel model = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    JTable examTable = new JTable(model);
    JScrollPane scrollPane = new JScrollPane(examTable);

    JButton refreshButton = new JButton("Refresh Examinations");

    refreshButton.addActionListener(e -> {
        model.setRowCount(0);

        ExamAllocationDAO examDAO = new ExamAllocationDAO();
        List<String[]> exams = examDAO.getAllExams();

        for (String[] exam : exams) {
            model.addRow(exam);
        }

        if (exams.isEmpty()) {
            JOptionPane.showMessageDialog(
                    panel,
                    "No examinations found. Check the examinations table in Supabase.",
                    "Examination Records",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    });

    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(title, BorderLayout.CENTER);
    topPanel.add(refreshButton, BorderLayout.EAST);

    panel.add(topPanel, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);

    refreshButton.doClick();

    return panel;
}
}
