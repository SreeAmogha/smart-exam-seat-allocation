
package org.example.member2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.example.member1.Student;

public class SeatAllocationUI extends JFrame {

    private ExamAllocationDAO dao = new ExamAllocationDAO();

    private JComboBox<String[]> examCombo;
    private JButton allocateButton;
    private JButton refreshButton;

    private JPanel hallPanel;
    private JLabel statusLabel;

    private List<Hall> currentHalls = new ArrayList<>();
    private List<Student> currentStudents = new ArrayList<>();

    public SeatAllocationUI() {

        setTitle("Smart Exam Seat Allocation");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel();

        topPanel.add(new JLabel("Select Examination:"));

        examCombo = new JComboBox<>();
        examCombo.setPreferredSize(new Dimension(300, 30));

        examCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus
                );

                if (value instanceof String[]) {
                    String[] exam = (String[]) value;
                    setText(exam[1] + " - " + exam[2]
                            + " (" + exam[3] + ")");
                }

                return this;
            }
        });

        topPanel.add(examCombo);

        allocateButton = new JButton("Allocate Seats");
        allocateButton.setEnabled(false);
        topPanel.add(allocateButton);

        refreshButton = new JButton("Refresh Layout");
        topPanel.add(refreshButton);

        statusLabel = new JLabel("Select an examination.");

        hallPanel = new JPanel();
        hallPanel.setLayout(new BoxLayout(hallPanel, BoxLayout.Y_AXIS));

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(hallPanel), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        loadExaminations();

        examCombo.addActionListener(e -> loadSelectedExam());

        allocateButton.addActionListener(e -> allocateSeats());

        refreshButton.addActionListener(e -> loadSelectedExam());
    }

    private void loadExaminations() {

        List<String[]> exams = dao.getAllExams();

        for (String[] exam : exams) {
            examCombo.addItem(exam);
        }

        if (exams.isEmpty()) {
            statusLabel.setText("No examinations found.");
        }
    }

    private void loadSelectedExam() {

        String[] selectedExam =
                (String[]) examCombo.getSelectedItem();

        if (selectedExam == null) {
            return;
        }

        String examId = selectedExam[0];
ExamAllocationDAO.ExamData data =
        dao.loadExamData(examId);

currentHalls = data.halls;
currentStudents = data.students;

int savedCount = data.allocatedCount;
        hallPanel.removeAll();

        allocateButton.setEnabled(
                !currentHalls.isEmpty()
                && !currentStudents.isEmpty()
                && savedCount == 0
        );

        statusLabel.setText(
                "Registered Students: " + currentStudents.size()
                + " | Assigned Halls: " + currentHalls.size()
                + " | Allocated: " + savedCount
        );

        if (currentHalls.isEmpty()) {
            hallPanel.add(new JLabel(
                    "No halls assigned to this examination."));
        } else {
            displayHallLayouts();
        }

        if (currentStudents.isEmpty()) {
            hallPanel.add(new JLabel(
                    "No students registered for this examination."));
        }

        hallPanel.revalidate();
        hallPanel.repaint();
    }

    private void allocateSeats() {

        String[] selectedExam =
                (String[]) examCombo.getSelectedItem();

        if (selectedExam == null) {
            return;
        }

        String examId = selectedExam[0];

        SeatAllocator allocator = new SeatAllocator();

        List<Student> unallocated =
                allocator.allocateAcrossHalls(
                        currentHalls, currentStudents);

        boolean saved = true;

        for (Hall hall : currentHalls) {
            if (!allocator.saveAllocations(examId, hall)) {
                saved = false;
            }
        }

        int allocatedCount =
                currentStudents.size() - unallocated.size();

        JOptionPane.showMessageDialog(
                this,
                "Allocation completed!\n"
                + "Allocated students: " + allocatedCount + "\n"
                + "Unallocated students: " + unallocated.size() + "\n"
                + "Database save: " + (saved ? "Successful" : "Failed")
        );

        loadSelectedExam();
    }

    private void displayHallLayouts() {

        hallPanel.removeAll();

        for (Hall hall : currentHalls) {

            JPanel oneHall = new JPanel(
                    new GridLayout(
                            hall.getNumberOfRows(),
                            hall.getSeatsPerRow(),
                            8, 8
                    )
            );

            oneHall.setBorder(
                    BorderFactory.createTitledBorder(
                            hall.getHallName()));

            RowNode row = hall.getRows().getHead();

            while (row != null) {

                SeatNode seat = row.getSeatList().getHead();

                while (seat != null) {

                    String text = "R" + seat.getRowNumber()
                            + " S" + seat.getSeatNumber();

                    if (seat.isOccupied()) {
                        text += "\n" + seat.getAllocatedUsn();
                    }

                    JLabel seatLabel = new JLabel(
                            text, SwingConstants.CENTER);

                    seatLabel.setOpaque(true);
                    seatLabel.setBorder(
                            BorderFactory.createLineBorder(Color.GRAY));

                    seatLabel.setBackground(
                            seat.isOccupied()
                            ? new Color(180, 230, 180)
                            : new Color(230, 230, 230)
                    );

                    oneHall.add(seatLabel);

                    seat = seat.getNext();
                }

                row = row.getNext();
            }

            hallPanel.add(oneHall);
            hallPanel.add(Box.createVerticalStrut(15));
        }

        hallPanel.revalidate();
        hallPanel.repaint();
    }
}
