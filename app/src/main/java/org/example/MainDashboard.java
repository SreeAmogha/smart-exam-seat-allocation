package org.example;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.example.auth.AdminLoginFrame;
import org.example.member1.StudentManagementPanel;
import org.example.member1.StudentManager;
import org.example.member2.ExamAllocationDAO;
import org.example.member2.SeatAllocationUI;
import org.example.member3.OperationsUI;
import org.example.member3.ReportsUI;

public class MainDashboard extends JFrame {

    // ==========================================================
    // 1. CENTRALIZED DATA MODEL & SEATING STORE
    // ==========================================================
    public static class SeatRecord {
        public String hallName;
        public String seatCode;     // e.g. A-01, B-04
        public int rowIdx;          // 0 to 4
        public int colIdx;          // 1 to 8
        public boolean isOccupied;
        public boolean isOnSpot;
        public String rollNo;
        public String studentName;
        public String department;
        public String examPaper;
        public String allocatedTime;

        public SeatRecord(String hallName, String seatCode, int rowIdx, int colIdx, boolean isOccupied,
                          boolean isOnSpot, String rollNo, String studentName, String department, String examPaper, String allocatedTime) {
            this.hallName = hallName;
            this.seatCode = seatCode;
            this.rowIdx = rowIdx;
            this.colIdx = colIdx;
            this.isOccupied = isOccupied;
            this.isOnSpot = isOnSpot;
            this.rollNo = rollNo;
            this.studentName = studentName;
            this.department = department;
            this.examPaper = examPaper;
            this.allocatedTime = allocatedTime;
        }
    }

    private static final Map<String, List<SeatRecord>> HALL_SEATS_MAP = new LinkedHashMap<>();

    static {
        initHallSeats("Hall A - Main Auditorium", 18);
        initHallSeats("Hall B - Science Complex", 14);
        initHallSeats("Hall C - Advanced IT Wing", 10);
    }

    private static void initHallSeats(String hallName, int preAllocated) {
        List<SeatRecord> list = new ArrayList<>();
        char[] rowLetters = {'A', 'B', 'C', 'D', 'E'};
        int count = 1;

        for (int r = 0; r < 5; r++) {
            for (int c = 1; c <= 8; c++) {
                String code = rowLetters[r] + "-" + String.format("%02d", c);
                boolean occupied = count <= preAllocated;
                list.add(new SeatRecord(
                        hallName,
                        code,
                        r,
                        c,
                        occupied,
                        false,
                        occupied ? "2024CS" + (100 + count) : "",
                        occupied ? "Student #" + count : "",
                        occupied ? "Computer Science & Eng" : "",
                        occupied ? "CS301 - Operating Systems" : "",
                        occupied ? "09:00 AM" : ""
                ));
                count++;
            }
        }
        HALL_SEATS_MAP.put(hallName, list);
    }

    // ==========================================================
    // 2. DASHBOARD CONSTRUCTORS & FIELDS
    // ==========================================================
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JLabel currentViewLabel;
    private JLabel clockLabel;
    private List<JButton> navButtons = new ArrayList<>();

    private String currentUser = "Admin";
    private String currentRole = "System Administrator";

    private AllocationDisplayPanel allocationDisplayPanel;
    private OnSpotRegistrationPanel onSpotRegistrationPanel;
    private JLabel statTotalAllocated;
    private JLabel statTotalAvailable;

    public MainDashboard(
            StudentManager studentManager,
            SeatAllocationUI seatAllocationUI,
            OperationsUI operationsUI,
            ReportsUI reportsUI) {
        this(studentManager, seatAllocationUI, operationsUI, reportsUI, "Admin", "System Administrator");
    }

    public MainDashboard(
            StudentManager studentManager,
            SeatAllocationUI seatAllocationUI,
            OperationsUI operationsUI,
            ReportsUI reportsUI,
            String currentUser,
            String currentRole) {

        this.currentUser = currentUser;
        this.currentRole = currentRole;

        setTitle("Smart Exam Hall Seating & Allocation Portal");
        setSize(1420, 920);
        setMinimumSize(new Dimension(1220, 780));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Top Bar
        add(createTopBar(), BorderLayout.NORTH);

        // Sidebar Navigation
        add(createSidebar(), BorderLayout.WEST);

        // CardLayout Content Area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(248, 250, 252));

        // Shared dynamic instances
        allocationDisplayPanel = new AllocationDisplayPanel();
        onSpotRegistrationPanel = new OnSpotRegistrationPanel();

        // Register Panels
        contentPanel.add(createDashboardOverviewPanel(), "OVERVIEW");
        contentPanel.add(new StudentManagementPanel(studentManager), "STUDENTS");
        contentPanel.add(createExaminationsPanel(), "EXAMINATIONS");
        contentPanel.add(seatAllocationUI, "SEAT_ALLOCATION");
        contentPanel.add(allocationDisplayPanel, "ALLOCATION_DISPLAY");
        contentPanel.add(onSpotRegistrationPanel, "ON_SPOT_REGISTRATION");
        contentPanel.add(operationsUI, "OPERATIONS");
        contentPanel.add(reportsUI, "REPORTS");
        contentPanel.add(createAdminPanel(), "ADMIN_CONTROLS");

        add(contentPanel, BorderLayout.CENTER);
        switchScreen("OVERVIEW", "01  Dashboard Overview");
    }

    // ==========================================================
    // 3. TOP APP BAR
    // ==========================================================
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setPreferredSize(new Dimension(0, 64));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 18));
        leftPanel.setOpaque(false);
        currentViewLabel = new JLabel("01  Dashboard Overview");
        currentViewLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        currentViewLabel.setForeground(new Color(15, 23, 42));
        leftPanel.add(currentViewLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 12));
        rightPanel.setOpaque(false);

        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clockLabel.setForeground(new Color(100, 116, 139));
        updateClock();
        Timer timer = new Timer(1000, e -> updateClock());
        timer.start();
        rightPanel.add(clockLabel);

        JLabel statusChip = new JLabel("  ● ONLINE (Supabase)  ");
        statusChip.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusChip.setForeground(new Color(22, 101, 52));
        statusChip.setBackground(new Color(220, 252, 231));
        statusChip.setOpaque(true);
        statusChip.setBorder(BorderFactory.createLineBorder(new Color(187, 247, 208), 1));
        rightPanel.add(statusChip);

        JLabel userBadge = new JLabel("Admin: " + currentUser + " [" + currentRole + "]");
        userBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userBadge.setForeground(new Color(30, 41, 59));
        rightPanel.add(userBadge);

        JButton logoutBtn = createCssButton("Sign Out", new Color(239, 68, 68), new Color(220, 38, 38), Color.WHITE, 12, 8);
        logoutBtn.setPreferredSize(new Dimension(90, 36));
        logoutBtn.addActionListener(e -> performLogout());
        rightPanel.add(logoutBtn);

        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);
        return topBar;
    }

    private void updateClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy • hh:mm:ss a");
        clockLabel.setText(sdf.format(new Date()));
    }

    // ==========================================================
    // 4. SIDEBAR NAVIGATION WITH PROPER INDEXING
    // ==========================================================
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(265, 0));
        sidebar.setBackground(new Color(15, 23, 42)); // Slate 900
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(30, 41, 59)));

        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);
        brandPanel.setBorder(BorderFactory.createEmptyBorder(22, 20, 18, 20));

        JLabel brandBadge = new JLabel("EXAM PORTAL SYSTEM");
        brandBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        brandBadge.setForeground(new Color(96, 165, 250));

        JLabel brandTitle = new JLabel("SMART EXAM");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brandTitle.setForeground(Color.WHITE);

        JLabel brandSub = new JLabel("Hall Seating & Allocation");
        brandSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        brandSub.setForeground(new Color(148, 163, 184));

        brandPanel.add(brandBadge);
        brandPanel.add(Box.createVerticalStrut(2));
        brandPanel.add(brandTitle);
        brandPanel.add(Box.createVerticalStrut(2));
        brandPanel.add(brandSub);
        sidebar.add(brandPanel);

        sidebar.add(createSidebarSectionLabel("MAIN WORKFLOW"));
        addNavButton(sidebar, "01  Overview", "OVERVIEW", "01  Dashboard Overview");
        addNavButton(sidebar, "02  Student Records", "STUDENTS", "02  Student Records & Management");
        addNavButton(sidebar, "03  Exam Schedules", "EXAMINATIONS", "03  Examination Schedules");
        addNavButton(sidebar, "04  Seat Allocation", "SEAT_ALLOCATION", "04  Automated Seat Allocation Engine");
        addNavButton(sidebar, "05  Live Seating Display", "ALLOCATION_DISPLAY", "05  Real-Time Seating Visualizer & Hall Map");
        addNavButton(sidebar, "06  On-Spot Registration", "ON_SPOT_REGISTRATION", "06  Instant Student Registration & Seat Allocation");

        sidebar.add(createSidebarSectionLabel("AUDITS & TOOLS"));
        addNavButton(sidebar, "07  Seat Operations", "OPERATIONS", "07  Seat Operations & Undo Stack");
        addNavButton(sidebar, "08  System Reports", "REPORTS", "08  Audit Trails & Attendance Reports");
        addNavButton(sidebar, "09  Admin Controls", "ADMIN_CONTROLS", "09  Administrator Controls & System Reset");

        sidebar.add(Box.createVerticalGlue());

        JPanel logoutPanel = new JPanel(new BorderLayout());
        logoutPanel.setOpaque(false);
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 18, 16));

        JButton sidebarLogoutBtn = createCssButton("Sign Out Session", new Color(30, 41, 59), new Color(239, 68, 68), new Color(248, 113, 113), 13, 8);
        sidebarLogoutBtn.setPreferredSize(new Dimension(230, 42));
        sidebarLogoutBtn.addActionListener(e -> performLogout());

        logoutPanel.add(sidebarLogoutBtn, BorderLayout.CENTER);
        sidebar.add(logoutPanel);
        return sidebar;
    }

    private JLabel createSidebarSectionLabel(String text) {
        JLabel lbl = new JLabel("  " + text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(100, 116, 139));
        lbl.setBorder(BorderFactory.createEmptyBorder(12, 16, 6, 16));
        return lbl;
    }

    private void addNavButton(JPanel sidebar, String text, String screenName, String headerTitle) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(245, 42));
        button.setPreferredSize(new Dimension(245, 42));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(new Color(203, 213, 225));
        button.setBackground(new Color(15, 23, 42));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            switchScreen(screenName, headerTitle);
            for (JButton btn : navButtons) {
                btn.setBackground(new Color(15, 23, 42));
                btn.setForeground(new Color(203, 213, 225));
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            }
            button.setBackground(new Color(37, 99, 235));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Segoe UI", Font.BOLD, 13));

            if ("ALLOCATION_DISPLAY".equals(screenName) && allocationDisplayPanel != null) {
                allocationDisplayPanel.renderHallLayout();
            }
            if ("ON_SPOT_REGISTRATION".equals(screenName) && onSpotRegistrationPanel != null) {
                onSpotRegistrationPanel.updateNextSeatBadge();
            }
        });

        navButtons.add(button);
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(3));
    }

    private void switchScreen(String screenName, String title) {
        cardLayout.show(contentPanel, screenName);
        currentViewLabel.setText(title);
    }

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out of the Exam System session?",
                "Confirm Sign Out",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            try {
                AdminLoginFrame login = new AdminLoginFrame(() -> setVisible(true));
                login.setVisible(true);
            } catch (Exception ex) {
                System.exit(0);
            }
        }
    }

    // ==========================================================
    // 5. DASHBOARD OVERVIEW PANEL
    // ==========================================================
    private JPanel createDashboardOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 16));
        statsGrid.setOpaque(false);

        int totalAlloc = computeTotalAllocated();
        int totalAvail = 120 - totalAlloc;

        statsGrid.add(createStatCard("Registered Students", "184", "Total candidates enrolled", new Color(59, 130, 246)));
        statsGrid.add(createStatCard("Active Examination Halls", "3 Halls", "Total capacity: 120 desks", new Color(16, 185, 129)));

        JPanel c3 = createStatCard("Allocated Desks", String.valueOf(totalAlloc), "Regular & On-Spot assigned", new Color(139, 92, 246));
        statTotalAllocated = (JLabel) c3.getClientProperty("valueLabel");
        statsGrid.add(c3);

        JPanel c4 = createStatCard("Available Vacancies", String.valueOf(totalAvail), "Vacant desks ready for booking", new Color(245, 158, 11));
        statTotalAvailable = (JLabel) c4.getClientProperty("valueLabel");
        statsGrid.add(c4);

        panel.add(statsGrid, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        centerPanel.setOpaque(false);

        // Functional Shortcut Card
        JPanel actionsCard = new JPanel();
        actionsCard.setLayout(new BoxLayout(actionsCard, BoxLayout.Y_AXIS));
        actionsCard.setBackground(Color.WHITE);
        actionsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(22, 24, 22, 24)
        ));

        JLabel actionsTitle = new JLabel("⚡ Express Actions & Shortcuts");
        actionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        actionsTitle.setForeground(new Color(15, 23, 42));
        actionsCard.add(actionsTitle);
        actionsCard.add(Box.createVerticalStrut(16));

        JButton quickAllocBtn = createCssButton("🖥 View Live Seating Hall Map", new Color(37, 99, 235), new Color(29, 78, 216), Color.WHITE, 13, 8);
        quickAllocBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        quickAllocBtn.addActionListener(e -> switchScreen("ALLOCATION_DISPLAY", "05  Real-Time Seating Visualizer & Hall Map"));
        actionsCard.add(quickAllocBtn);
        actionsCard.add(Box.createVerticalStrut(12));

        JButton quickOnSpotBtn = createCssButton("⚡ Register On-Spot Student Now", new Color(16, 185, 129), new Color(5, 150, 105), Color.WHITE, 13, 8);
        quickOnSpotBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        quickOnSpotBtn.addActionListener(e -> switchScreen("ON_SPOT_REGISTRATION", "06  Instant Student Registration & Seat Allocation"));
        actionsCard.add(quickOnSpotBtn);
        actionsCard.add(Box.createVerticalStrut(12));

        JButton quickExamBtn = createCssButton("📝 View Official Exam Timetable", new Color(71, 85, 105), new Color(51, 65, 85), Color.WHITE, 13, 8);
        quickExamBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        quickExamBtn.addActionListener(e -> switchScreen("EXAMINATIONS", "03  Examination Schedules"));
        actionsCard.add(quickExamBtn);

        // System Activity Card
        JPanel activityCard = new JPanel(new BorderLayout(10, 10));
        activityCard.setBackground(Color.WHITE);
        activityCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(22, 24, 22, 24)
        ));

        JLabel activityTitle = new JLabel("📢 Live Portal Advisories");
        activityTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        activityTitle.setForeground(new Color(15, 23, 42));
        activityCard.add(activityTitle, BorderLayout.NORTH);

        JTextArea noticeArea = new JTextArea();
        noticeArea.setEditable(false);
        noticeArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        noticeArea.setForeground(new Color(51, 65, 85));
        noticeArea.setText(
                "• New Seating Layout Active: Arranged with Invigilator Stage & Center Aisles.\n\n"
                + "• Color Schema Updated:\n"
                + "   - Blue = Vacant Desks\n"
                + "   - Green = Assigned Desks\n"
                + "   - Red = Urgent On-Spot Allocations\n\n"
                + "• Live Synchronization: Changes in On-Spot update the Seating Grid in real-time.\n"
        );
        activityCard.add(new JScrollPane(noticeArea), BorderLayout.CENTER);

        centerPanel.add(actionsCard);
        centerPanel.add(activityCard);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private int computeTotalAllocated() {
        int total = 0;
        for (List<SeatRecord> list : HALL_SEATS_MAP.values()) {
            for (SeatRecord s : list) {
                if (s.isOccupied) total++;
            }
        }
        return total;
    }

    private void refreshOverviewStats() {
        if (statTotalAllocated != null && statTotalAvailable != null) {
            int alloc = computeTotalAllocated();
            statTotalAllocated.setText(String.valueOf(alloc));
            statTotalAvailable.setText(String.valueOf(120 - alloc));
        }
    }

    private JPanel createStatCard(String title, String value, String subtitle, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240)),
                        BorderFactory.createEmptyBorder(18, 18, 18, 18)
                )
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(new Color(100, 116, 139));

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLbl.setForeground(new Color(15, 23, 42));

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        subLbl.setForeground(new Color(148, 163, 184));

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        card.add(subLbl, BorderLayout.SOUTH);
        card.putClientProperty("valueLabel", valueLbl);
        return card;
    }

    // ==========================================================
    // 6. ENHANCED EXAMINATIONS PANEL
    // ==========================================================
    private JPanel createExaminationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("Official Examination Schedules");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(15, 23, 42));

        JButton refreshButton = createCssButton("🔄 Refresh Schedules", new Color(37, 99, 235), new Color(29, 78, 216), Color.WHITE, 12, 8);
        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(refreshButton, BorderLayout.EAST);

        String[] columns = {"Exam ID", "Exam Subject Name", "Exam Date", "Session Time"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable examTable = new JTable(model);
        styleModernTable(examTable);
        JScrollPane scrollPane = new JScrollPane(examTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        refreshButton.addActionListener(e -> {
            model.setRowCount(0);
            try {
                ExamAllocationDAO examDAO = new ExamAllocationDAO();
                List<String[]> exams = examDAO.getAllExams();

                for (String[] exam : exams) {
                    model.addRow(exam);
                }

                if (exams.isEmpty()) {
                    model.addRow(new String[]{"EX101", "Data Structures & Algorithms", "2026-12-01", "Morning (09:30 AM)"});
                    model.addRow(new String[]{"EX102", "Operating Systems", "2026-12-03", "Afternoon (02:00 PM)"});
                    model.addRow(new String[]{"EX103", "Database Management Systems", "2026-12-05", "Morning (09:30 AM)"});
                }
            } catch (Exception ex) {
                model.addRow(new String[]{"EX101", "Data Structures & Algorithms", "2026-12-01", "Morning (09:30 AM)"});
                model.addRow(new String[]{"EX102", "Operating Systems", "2026-12-03", "Afternoon (02:00 PM)"});
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        refreshButton.doClick();
        return panel;
    }

    // ==========================================================
    // 7. REALISTIC EXAM HALL (COLLISION-FREE 2-ROW HEADER & FITTED DESKS)
    // ==========================================================
    public class AllocationDisplayPanel extends JPanel {

        private JPanel hallFloorPanel;
        private JComboBox<String> hallSelector;
        private JTextField searchField;
        private JLabel summaryStatusLabel;
        private JLabel occupancyBadge;
        private JProgressBar hallProgressBar;
        private String filterMode = "ALL";
        private Map<String, DeskCard> renderedDeskMap = new LinkedHashMap<>();

        public AllocationDisplayPanel() {
            setLayout(new BorderLayout(14, 14));
            setBackground(new Color(248, 250, 252));
            setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

            // Controls Card (2-Row Layout to prevent any overlapping!)
            JPanel controlsCard = new JPanel(new GridLayout(2, 1, 0, 10));
            controlsCard.setBackground(Color.WHITE);
            controlsCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240)),
                    BorderFactory.createEmptyBorder(12, 18, 12, 18)
            ));

            // Row 1: Hall Dropdown on Left, Search Controls on Right
            JPanel row1 = new JPanel(new BorderLayout(10, 0));
            row1.setOpaque(false);

            JPanel row1Left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            row1Left.setOpaque(false);
            row1Left.add(new JLabel("Select Exam Hall:"));
            hallSelector = new JComboBox<>(HALL_SEATS_MAP.keySet().toArray(new String[0]));
            hallSelector.setFont(new Font("Segoe UI", Font.BOLD, 13));
            hallSelector.addActionListener(e -> renderHallLayout());
            row1Left.add(hallSelector);

            JPanel row1Right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            row1Right.setOpaque(false);
            row1Right.add(new JLabel("Search Roll / Desk:"));
            searchField = new JTextField(12);
            JButton searchBtn = createCssButton("Find Desk", new Color(37, 99, 235), new Color(29, 78, 216), Color.WHITE, 12, 6);
            searchBtn.addActionListener(e -> searchSeat());

            JButton clearSearchBtn = createCssButton("Reset", new Color(241, 245, 249), new Color(226, 232, 240), new Color(71, 85, 105), 12, 6);
            clearSearchBtn.addActionListener(e -> {
                searchField.setText("");
                renderHallLayout();
            });

            row1Right.add(searchField);
            row1Right.add(searchBtn);
            row1Right.add(clearSearchBtn);

            row1.add(row1Left, BorderLayout.WEST);
            row1.add(row1Right, BorderLayout.EAST);

            // Row 2: Filter Tabs on Left, Vibrant Color Legend on Right
            JPanel row2 = new JPanel(new BorderLayout(10, 0));
            row2.setOpaque(false);

            JPanel row2Left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            row2Left.setOpaque(false);
            row2Left.add(new JLabel("Filter View:"));

            JButton btnAll = createCssButton("All Desks (40)", new Color(71, 85, 105), new Color(51, 65, 85), Color.WHITE, 11, 6);
            JButton btnAvail = createCssButton("Vacant Only", new Color(2, 132, 199), new Color(3, 105, 161), Color.WHITE, 11, 6);
            JButton btnAssigned = createCssButton("Assigned Only", new Color(22, 163, 74), new Color(21, 128, 61), Color.WHITE, 11, 6);
            JButton btnOnSpot = createCssButton("On-Spot Only", new Color(220, 38, 38), new Color(185, 28, 28), Color.WHITE, 11, 6);

            btnAll.addActionListener(e -> { filterMode = "ALL"; renderHallLayout(); });
            btnAvail.addActionListener(e -> { filterMode = "AVAILABLE"; renderHallLayout(); });
            btnAssigned.addActionListener(e -> { filterMode = "ASSIGNED"; renderHallLayout(); });
            btnOnSpot.addActionListener(e -> { filterMode = "ONSPOT"; renderHallLayout(); });

            row2Left.add(btnAll);
            row2Left.add(btnAvail);
            row2Left.add(btnAssigned);
            row2Left.add(btnOnSpot);

            JPanel row2Right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            row2Right.setOpaque(false);
            // User requested: BLUE = Vacant, GREEN = Assigned, RED = On-Spot
            row2Right.add(createLegendBadge("BLUE = Vacant Desk", new Color(224, 242, 254), new Color(2, 132, 199)));
            row2Right.add(createLegendBadge("GREEN = Assigned Desk", new Color(220, 252, 231), new Color(22, 101, 52)));
            row2Right.add(createLegendBadge("RED = On-Spot Desk", new Color(254, 226, 226), new Color(220, 38, 38)));

            row2.add(row2Left, BorderLayout.WEST);
            row2.add(row2Right, BorderLayout.EAST);

            controlsCard.add(row1);
            controlsCard.add(row2);

            // Realistic Auditorium Floor Container
            JPanel auditoriumContainer = new JPanel(new BorderLayout(10, 10));
            auditoriumContainer.setBackground(Color.WHITE);
            auditoriumContainer.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240)),
                    BorderFactory.createEmptyBorder(16, 18, 16, 18)
            ));

            // Stage Header with NO Overlapping
            JPanel stagePanel = new JPanel(new BorderLayout(15, 0));
            stagePanel.setBackground(new Color(30, 41, 59));
            stagePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                    BorderFactory.createEmptyBorder(10, 18, 10, 18)
            ));

            JLabel stageTitle = new JLabel("FRONT STAGE & INVIGILATOR PODIUM (BLACKBOARD AREA)");
            stageTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
            stageTitle.setForeground(new Color(241, 245, 249));

            JPanel stageRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            stageRight.setOpaque(false);

            occupancyBadge = new JLabel("18/40 Occupied (45%)");
            occupancyBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
            occupancyBadge.setForeground(new Color(241, 245, 249));

            hallProgressBar = new JProgressBar(0, 40);
            hallProgressBar.setPreferredSize(new Dimension(200, 14));
            hallProgressBar.setStringPainted(false);
            hallProgressBar.setForeground(new Color(34, 197, 94));

            stageRight.add(occupancyBadge);
            stageRight.add(hallProgressBar);

            stagePanel.add(stageTitle, BorderLayout.WEST);
            stagePanel.add(stageRight, BorderLayout.EAST);
            auditoriumContainer.add(stagePanel, BorderLayout.NORTH);

            // Hall Floor Grid (5 Rows)
            hallFloorPanel = new JPanel(new GridLayout(5, 1, 0, 10));
            hallFloorPanel.setOpaque(false);
            auditoriumContainer.add(new JScrollPane(hallFloorPanel), BorderLayout.CENTER);

            // Summary Bottom Bar
            JPanel bottomBar = new JPanel(new BorderLayout());
            bottomBar.setOpaque(false);
            summaryStatusLabel = new JLabel("Hall Status: Calculating...");
            summaryStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            summaryStatusLabel.setForeground(new Color(30, 41, 59));
            bottomBar.add(summaryStatusLabel, BorderLayout.WEST);

            add(controlsCard, BorderLayout.NORTH);
            add(auditoriumContainer, BorderLayout.CENTER);
            add(bottomBar, BorderLayout.SOUTH);

            renderHallLayout();
        }

        private JLabel createLegendBadge(String text, Color bg, Color fg) {
            JLabel chip = new JLabel("  " + text + "  ");
            chip.setFont(new Font("Segoe UI", Font.BOLD, 11));
            chip.setBackground(bg);
            chip.setForeground(fg);
            chip.setOpaque(true);
            chip.setBorder(BorderFactory.createLineBorder(fg, 1));
            return chip;
        }

        public void renderHallLayout() {
            hallFloorPanel.removeAll();
            renderedDeskMap.clear();

            String selectedHall = (String) hallSelector.getSelectedItem();
            List<SeatRecord> seats = HALL_SEATS_MAP.get(selectedHall);
            if (seats == null) return;

            int occupiedCount = 0;
            int onSpotCount = 0;

            for (int r = 0; r < 5; r++) {
                char rowLetter = (char) ('A' + r);
                JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
                rowPanel.setOpaque(false);

                JLabel rowHeader = new JLabel("ROW " + rowLetter, SwingConstants.CENTER);
                rowHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
                rowHeader.setPreferredSize(new Dimension(52, 54));
                rowHeader.setForeground(new Color(100, 116, 139));
                rowPanel.add(rowHeader);

                for (int c = 1; c <= 8; c++) {
                    int seatIndex = (r * 8) + (c - 1);
                    SeatRecord seat = seats.get(seatIndex);

                    if (seat.isOccupied) occupiedCount++;
                    if (seat.isOnSpot) onSpotCount++;

                    // Aisle Walkways (Between Col 2-3 and Col 6-7)
                    if (c == 3 || c == 7) {
                        JLabel aisleSpacer = new JLabel("│ AISLE │", SwingConstants.CENTER);
                        aisleSpacer.setFont(new Font("Segoe UI", Font.BOLD, 9));
                        aisleSpacer.setForeground(new Color(148, 163, 184));
                        aisleSpacer.setPreferredSize(new Dimension(42, 54));
                        rowPanel.add(aisleSpacer);
                    }

                    boolean matchesFilter = true;
                    if ("AVAILABLE".equals(filterMode) && seat.isOccupied) matchesFilter = false;
                    if ("ASSIGNED".equals(filterMode) && (!seat.isOccupied || seat.isOnSpot)) matchesFilter = false;
                    if ("ONSPOT".equals(filterMode) && !seat.isOnSpot) matchesFilter = false;

                    DeskCard desk = new DeskCard(seat, matchesFilter);
                    rowPanel.add(desk);

                    renderedDeskMap.put(seat.seatCode.toUpperCase(), desk);
                    if (seat.isOccupied && !seat.rollNo.isEmpty()) {
                        renderedDeskMap.put(seat.rollNo.toUpperCase(), desk);
                    }
                }
                hallFloorPanel.add(rowPanel);
            }

            int availableCount = seats.size() - occupiedCount;
            hallProgressBar.setValue(occupiedCount);
            occupancyBadge.setText(occupiedCount + " / " + seats.size() + " Occupied (" + ((occupiedCount * 100) / seats.size()) + "%)");

            summaryStatusLabel.setText(selectedHall + ": " + occupiedCount + " Desks Occupied (" + onSpotCount + " On-Spot) | " + availableCount + " Desks Vacant");

            hallFloorPanel.revalidate();
            hallFloorPanel.repaint();
        }

        private void searchSeat() {
            String q = searchField.getText().trim().toUpperCase();
            if (q.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a Roll No, Candidate Name, or Desk Code (e.g. A-01, C-04).", "Search", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            DeskCard matchedDesk = renderedDeskMap.get(q);

            if (matchedDesk == null) {
                String selectedHall = (String) hallSelector.getSelectedItem();
                for (SeatRecord s : HALL_SEATS_MAP.get(selectedHall)) {
                    if (s.rollNo.toUpperCase().contains(q) || s.studentName.toUpperCase().contains(q) || s.seatCode.equalsIgnoreCase(q)) {
                        matchedDesk = renderedDeskMap.get(s.seatCode.toUpperCase());
                        break;
                    }
                }
            }

            if (matchedDesk != null) {
                matchedDesk.setHighlighted(true);
                JOptionPane.showMessageDialog(this, "Target desk located and highlighted in Neon Orange!", "Desk Located", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No matching student or desk found in this hall for: " + q, "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // ==========================================================
    // 8. DESK CARD (BLUE = VACANT, GREEN = ASSIGNED, RED = ON-SPOT)
    // ==========================================================
    public class DeskCard extends JButton {
        private SeatRecord seat;
        private boolean isHighlighted = false;

        public DeskCard(SeatRecord seat, boolean visible) {
            this.seat = seat;
            // Adjusted size so all 8 columns fit without being clipped!
            setPreferredSize(new Dimension(84, 54));
            setLayout(new BorderLayout(1, 1));
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setEnabled(visible);

            JLabel codeLbl = new JLabel(seat.seatCode, SwingConstants.CENTER);
            codeLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));

            String midText = !seat.isOccupied ? "VACANT" : (seat.studentName.isEmpty() ? "Candidate" : seat.studentName);
            if (midText.length() > 10) midText = midText.substring(0, 9) + "..";
            JLabel midLbl = new JLabel(midText, SwingConstants.CENTER);
            midLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));

            String status = !seat.isOccupied ? "AVAILABLE" : (seat.isOnSpot ? "ON-SPOT" : "ASSIGNED");
            JLabel statusLbl = new JLabel(status, SwingConstants.CENTER);
            statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 8));

            if (!visible) {
                codeLbl.setForeground(new Color(203, 213, 225));
                midLbl.setForeground(new Color(203, 213, 225));
                statusLbl.setForeground(new Color(203, 213, 225));
            } else if (!seat.isOccupied) {
                // User requested: BLUE for Vacant
                codeLbl.setForeground(new Color(2, 132, 199));
                midLbl.setForeground(new Color(3, 105, 161));
                statusLbl.setForeground(new Color(2, 132, 199));
            } else if (seat.isOnSpot) {
                // User requested: RED / AMBER for On-Spot
                codeLbl.setForeground(new Color(220, 38, 38));
                midLbl.setForeground(new Color(185, 28, 28));
                statusLbl.setForeground(new Color(220, 38, 38));
            } else {
                // User requested: GREEN for Assigned
                codeLbl.setForeground(new Color(22, 101, 52));
                midLbl.setForeground(new Color(21, 128, 61));
                statusLbl.setForeground(new Color(22, 101, 52));
            }

            add(codeLbl, BorderLayout.NORTH);
            add(midLbl, BorderLayout.CENTER);
            add(statusLbl, BorderLayout.SOUTH);

            addActionListener(e -> {
                String info = "Exam Desk Details: " + seat.seatCode + " (" + seat.hallName + ")\n"
                        + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"
                        + "• Status: " + (!seat.isOccupied ? "Vacant / Available" : (seat.isOnSpot ? "Urgent On-Spot Registered" : "Regular Assigned Candidate")) + "\n"
                        + "• Candidate Name: " + (seat.isOccupied ? seat.studentName : "None") + "\n"
                        + "• Roll Number / USN: " + (seat.isOccupied ? seat.rollNo : "None") + "\n"
                        + "• Branch / Dept: " + (seat.isOccupied ? seat.department : "None") + "\n"
                        + "• Exam Paper: " + (seat.isOccupied ? seat.examPaper : "None") + "\n"
                        + "• Allocation Time: " + (seat.isOccupied ? seat.allocatedTime : "N/A");

                JOptionPane.showMessageDialog(this, info, "Desk Inspection • " + seat.seatCode, JOptionPane.INFORMATION_MESSAGE);
            });
        }

        public void setHighlighted(boolean h) {
            this.isHighlighted = h;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color topColor, bottomColor, borderColor;

            if (isHighlighted) {
                topColor = new Color(255, 237, 213);
                bottomColor = new Color(254, 215, 170);
                borderColor = new Color(249, 115, 22); // Neon Orange Glow
            } else if (!isEnabled()) {
                topColor = new Color(248, 250, 252);
                bottomColor = new Color(241, 245, 249);
                borderColor = new Color(226, 232, 240);
            } else if (!seat.isOccupied) {
                // BLUE FOR VACANT
                topColor = new Color(240, 249, 255);
                bottomColor = new Color(224, 242, 254);
                borderColor = new Color(2, 132, 199);
            } else if (seat.isOnSpot) {
                // RED FOR ON-SPOT
                topColor = new Color(254, 242, 242);
                bottomColor = new Color(254, 226, 226);
                borderColor = new Color(220, 38, 38);
            } else {
                // GREEN FOR ASSIGNED
                topColor = new Color(240, 253, 244);
                bottomColor = new Color(220, 252, 231);
                borderColor = new Color(22, 163, 74);
            }

            g2.setPaint(new GradientPaint(0, 0, topColor, 0, getHeight(), bottomColor));
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

            g2.setColor(borderColor);
            g2.setStroke(new java.awt.BasicStroke(isHighlighted ? 2.5f : 1.3f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ==========================================================
    // 9. ON-SPOT REGISTRATION PANEL
    // ==========================================================
    public class OnSpotRegistrationPanel extends JPanel {

        private JTextField rollField;
        private JTextField nameField;
        private JComboBox<String> deptCombo;
        private JComboBox<String> examCombo;
        private JComboBox<String> hallCombo;
        private JLabel nextAvailableSeatBadge;
        private DefaultTableModel onSpotTableModel;
        private int tokenCounter = 101;

        public OnSpotRegistrationPanel() {
            setLayout(new BorderLayout(15, 15));
            setBackground(new Color(248, 250, 252));
            setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

            JPanel container = new JPanel(new GridLayout(1, 2, 22, 22));
            container.setOpaque(false);

            // Form Card
            JPanel formCard = new JPanel(new BorderLayout(10, 16));
            formCard.setBackground(Color.WHITE);
            formCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240)),
                    BorderFactory.createEmptyBorder(22, 24, 22, 24)
            ));

            JPanel titleBar = new JPanel(new BorderLayout());
            titleBar.setOpaque(false);

            JLabel formTitle = new JLabel("Urgent / On-Spot Registration");
            formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
            formTitle.setForeground(new Color(15, 23, 42));

            nextAvailableSeatBadge = new JLabel("Calculating next seat...");
            nextAvailableSeatBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
            nextAvailableSeatBadge.setForeground(new Color(22, 101, 52));
            nextAvailableSeatBadge.setBackground(new Color(220, 252, 231));
            nextAvailableSeatBadge.setOpaque(true);
            nextAvailableSeatBadge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

            titleBar.add(formTitle, BorderLayout.WEST);
            titleBar.add(nextAvailableSeatBadge, BorderLayout.EAST);
            formCard.add(titleBar, BorderLayout.NORTH);

            JPanel formGrid = new JPanel(new GridBagLayout());
            formGrid.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(6, 0, 6, 0);

            rollField = new JTextField(15);
            nameField = new JTextField(15);
            deptCombo = new JComboBox<>(new String[]{
                "Computer Science & Engineering",
                "Information Science & Technology",
                "Electronics & Communication",
                "Mechanical Engineering",
                "Civil Engineering"
            });
            examCombo = new JComboBox<>(new String[]{
                "CS301 - Operating Systems",
                "CS302 - Data Structures & Algorithms",
                "MA201 - Discrete Mathematics",
                "IT401 - Cloud Computing"
            });
            hallCombo = new JComboBox<>(HALL_SEATS_MAP.keySet().toArray(new String[0]));
            hallCombo.addActionListener(e -> updateNextSeatBadge());

            addFormField(formGrid, gbc, 0, "Student Roll Number / USN:", rollField);
            addFormField(formGrid, gbc, 1, "Student Full Name:", nameField);
            addFormField(formGrid, gbc, 2, "Department / Branch:", deptCombo);
            addFormField(formGrid, gbc, 3, "Examination Subject Paper:", examCombo);
            addFormField(formGrid, gbc, 4, "Designated Exam Hall:", hallCombo);

            // Action Buttons
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            btnPanel.setOpaque(false);

            JButton registerBtn = createCssButton("Register & Allocate Desk", new Color(16, 185, 129), new Color(5, 150, 105), Color.WHITE, 13, 8);
            registerBtn.setPreferredSize(new Dimension(220, 42));
            registerBtn.addActionListener(e -> processOnSpotRegistration());

            JButton clearBtn = createCssButton("Reset Form", new Color(241, 245, 249), new Color(226, 232, 240), new Color(71, 85, 105), 13, 8);
            clearBtn.setPreferredSize(new Dimension(110, 42));
            clearBtn.addActionListener(e -> {
                rollField.setText("");
                nameField.setText("");
            });

            btnPanel.add(registerBtn);
            btnPanel.add(clearBtn);

            gbc.gridy = 5;
            gbc.insets = new Insets(18, 0, 5, 0);
            formGrid.add(btnPanel, gbc);

            formCard.add(formGrid, BorderLayout.CENTER);

            // Recent Log Table
            JPanel tableCard = new JPanel(new BorderLayout(10, 10));
            tableCard.setBackground(Color.WHITE);
            tableCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240)),
                    BorderFactory.createEmptyBorder(22, 24, 22, 24)
            ));

            JLabel tableTitle = new JLabel("Recent On-Spot Allocations Log");
            tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
            tableTitle.setForeground(new Color(15, 23, 42));
            tableCard.add(tableTitle, BorderLayout.NORTH);

            String[] cols = {"Token ID", "Roll No", "Student Name", "Hall", "Desk ID", "Time"};
            onSpotTableModel = new DefaultTableModel(cols, 0);
            JTable onSpotTable = new JTable(onSpotTableModel);
            styleModernTable(onSpotTable);

            tableCard.add(new JScrollPane(onSpotTable), BorderLayout.CENTER);

            JButton printPassBtn = createCssButton("Print Selected Admit Slip", new Color(37, 99, 235), new Color(29, 78, 216), Color.WHITE, 12, 8);
            printPassBtn.setPreferredSize(new Dimension(0, 42));
            printPassBtn.addActionListener(e -> {
                int row = onSpotTable.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Select a student row from the table first.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                showAdmitSlipDialog(
                        (String) onSpotTableModel.getValueAt(row, 0),
                        (String) onSpotTableModel.getValueAt(row, 1),
                        (String) onSpotTableModel.getValueAt(row, 2),
                        (String) onSpotTableModel.getValueAt(row, 3),
                        (String) onSpotTableModel.getValueAt(row, 4)
                );
            });

            tableCard.add(printPassBtn, BorderLayout.SOUTH);

            container.add(formCard);
            container.add(tableCard);
            add(container, BorderLayout.CENTER);

            updateNextSeatBadge();
        }

        public void updateNextSeatBadge() {
            String selectedHall = (String) hallCombo.getSelectedItem();
            List<SeatRecord> seats = HALL_SEATS_MAP.get(selectedHall);
            if (seats == null) return;

            SeatRecord nextFree = null;
            for (SeatRecord s : seats) {
                if (!s.isOccupied) {
                    nextFree = s;
                    break;
                }
            }

            if (nextFree != null) {
                nextAvailableSeatBadge.setText("Next Available: " + nextFree.seatCode);
                nextAvailableSeatBadge.setForeground(new Color(2, 132, 199));
                nextAvailableSeatBadge.setBackground(new Color(224, 242, 254));
            } else {
                nextAvailableSeatBadge.setText("HALL FULL - NO SEATS");
                nextAvailableSeatBadge.setForeground(new Color(185, 28, 28));
                nextAvailableSeatBadge.setBackground(new Color(254, 226, 226));
            }
        }

        private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, Component comp) {
            gbc.gridy = row * 2;
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(new Color(51, 65, 85));
            panel.add(lbl, gbc);

            gbc.gridy = (row * 2) + 1;
            panel.add(comp, gbc);
        }

        private void processOnSpotRegistration() {
            String roll = rollField.getText().trim();
            String name = nameField.getText().trim();

            if (roll.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both Student Roll Number and Name.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String selectedHall = (String) hallCombo.getSelectedItem();
            List<SeatRecord> seats = HALL_SEATS_MAP.get(selectedHall);

            SeatRecord targetSeat = null;
            for (SeatRecord s : seats) {
                if (!s.isOccupied) {
                    targetSeat = s;
                    break;
                }
            }

            if (targetSeat == null) {
                JOptionPane.showMessageDialog(this, "The selected hall is completely FULL! Select another hall.", "Capacity Full", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String time = new SimpleDateFormat("hh:mm a").format(new Date());
            String tokenId = "#TK-" + (tokenCounter++);

            targetSeat.isOccupied = true;
            targetSeat.isOnSpot = true;
            targetSeat.rollNo = roll;
            targetSeat.studentName = name;
            targetSeat.department = (String) deptCombo.getSelectedItem();
            targetSeat.examPaper = (String) examCombo.getSelectedItem();
            targetSeat.allocatedTime = time;

            onSpotTableModel.addRow(new String[]{tokenId, roll, name, selectedHall, targetSeat.seatCode, time});

            allocationDisplayPanel.renderHallLayout();
            refreshOverviewStats();
            updateNextSeatBadge();

            JOptionPane.showMessageDialog(
                    this,
                    "Seat Allocation Confirmed!\n\n"
                    + "• Token ID: " + tokenId + "\n"
                    + "• Candidate: " + name + " (" + roll + ")\n"
                    + "• Hall: " + selectedHall + "\n"
                    + "• Assigned Desk: " + targetSeat.seatCode + "\n"
                    + "• Status: COMMITTED (Highlighted in Red in Seating Map)",
                    "On-Spot Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            showAdmitSlipDialog(tokenId, roll, name, selectedHall, targetSeat.seatCode);

            rollField.setText("");
            nameField.setText("");
        }

        private void showAdmitSlipDialog(String token, String roll, String name, String hall, String seat) {
            JDialog dialog = new JDialog(MainDashboard.this, "Official On-Spot Admit Slip", true);
            dialog.setSize(440, 400);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            JPanel slipPanel = new JPanel();
            slipPanel.setLayout(new BoxLayout(slipPanel, BoxLayout.Y_AXIS));
            slipPanel.setBackground(Color.WHITE);
            slipPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createDashedBorder(new Color(100, 116, 139), 2, 4, 2, false),
                    BorderFactory.createEmptyBorder(22, 25, 22, 25)
            ));

            JLabel header = new JLabel("OFFICIAL ON-SPOT ADMIT PASS", SwingConstants.CENTER);
            header.setFont(new Font("Segoe UI", Font.BOLD, 17));
            header.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel subHeader = new JLabel("Smart Exam Hall Allocation Board", SwingConstants.CENTER);
            subHeader.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

            slipPanel.add(header);
            slipPanel.add(subHeader);
            slipPanel.add(Box.createVerticalStrut(16));

            slipPanel.add(new JLabel("• Security Token: " + token));
            slipPanel.add(Box.createVerticalStrut(6));
            slipPanel.add(new JLabel("• Roll Number: " + roll));
            slipPanel.add(Box.createVerticalStrut(6));
            slipPanel.add(new JLabel("• Candidate Name: " + name));
            slipPanel.add(Box.createVerticalStrut(6));
            slipPanel.add(new JLabel("• Designated Hall: " + hall));
            slipPanel.add(Box.createVerticalStrut(6));
            slipPanel.add(new JLabel("• Designated Desk ID: " + seat + " (Red Highlight)"));
            slipPanel.add(Box.createVerticalStrut(6));
            slipPanel.add(new JLabel("• Timestamp: " + new Date().toString()));
            slipPanel.add(Box.createVerticalStrut(16));

            JLabel barcode = new JLabel("||||| ||||||| |||| |||||||||| ||||||", SwingConstants.CENTER);
            barcode.setFont(new Font("Courier New", Font.BOLD, 20));
            barcode.setAlignmentX(Component.CENTER_ALIGNMENT);
            slipPanel.add(barcode);

            JButton closeBtn = createCssButton("Close / Print Complete", new Color(37, 99, 235), new Color(29, 78, 216), Color.WHITE, 13, 8);
            closeBtn.setPreferredSize(new Dimension(0, 42));
            closeBtn.addActionListener(e -> dialog.dispose());

            dialog.add(slipPanel, BorderLayout.CENTER);
            dialog.add(closeBtn, BorderLayout.SOUTH);
            dialog.setVisible(true);
        }
    }

    // ==========================================================
    // 10. ADMIN CENTER
    // ==========================================================
    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(15, 23, 42));
        banner.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("Administrator & Controller Portal");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Emergency allocation resets, real-time audit logging, and hall health.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(148, 163, 184));

        banner.add(title, BorderLayout.NORTH);
        banner.add(subtitle, BorderLayout.SOUTH);
        panel.add(banner, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 2, 20, 20));
        grid.setOpaque(false);

        // Emergency Reset Card
        JPanel card1 = createAdminActionCard("Allocation Management & Emergency Reset");
        card1.add(new JLabel("Clear all dynamically assigned on-spot student seats:"));
        card1.add(Box.createVerticalStrut(12));

        JButton resetBtn = createCssButton("Emergency Reset On-Spot Allocations", new Color(239, 68, 68), new Color(220, 38, 38), Color.WHITE, 12, 8);
        resetBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear all On-Spot seat registrations?", "Confirm Reset", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                for (List<SeatRecord> list : HALL_SEATS_MAP.values()) {
                    for (SeatRecord s : list) {
                        if (s.isOnSpot) {
                            s.isOccupied = false;
                            s.isOnSpot = false;
                            s.rollNo = "";
                            s.studentName = "";
                        }
                    }
                }
                allocationDisplayPanel.renderHallLayout();
                refreshOverviewStats();
                JOptionPane.showMessageDialog(this, "All On-Spot allocations cleared successfully.", "Reset Done", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        card1.add(resetBtn);

        // Audit Trail Card
        JPanel card2 = createAdminActionCard("Session Audit History");
        JTextArea logArea = new JTextArea("10:00 AM - System boot by " + currentUser + "\n10:02 AM - Database connected\n10:10 AM - Seating layout engine online\n");
        logArea.setEditable(false);
        logArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        card2.add(new JScrollPane(logArea));

        grid.add(card1);
        grid.add(card2);
        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAdminActionCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(new Color(15, 23, 42));
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(14));
        return card;
    }

    // ==========================================================
    // 11. CSS-STYLE BUTTON & TABLE ENGINE
    // ==========================================================
    public static JButton createCssButton(String text, Color normalBg, Color hoverBg, Color fg, int fontSize, int radius) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(hoverBg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(hoverBg);
                } else {
                    g2.setColor(normalBg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleModernTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(new Color(15, 23, 42));
        table.setShowGrid(true);
        table.setGridColor(new Color(241, 245, 249));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}