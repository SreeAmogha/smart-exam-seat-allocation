package org.example;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.example.auth.AdminLoginFrame;
import org.example.member1.StudentManager;
import org.example.member2.HallDAO;
import org.example.member2.SeatAllocationUI;
import org.example.member3.OperationHistoryManager;
import org.example.member3.OperationStack;
import org.example.member3.OperationsDAO;
import org.example.member3.OperationsUI;
import org.example.member3.ReportGenerator;
import org.example.member3.ReportsUI;
import org.example.member3.SeatOperationManager;
import org.example.services.AllocationService;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HallDAO hallDAO = new HallDAO();
            if (!hallDAO.testConnection()) {
                JOptionPane.showMessageDialog(null,
                        "Could not connect to Supabase. Check database settings.",
                        "Database Connection Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AdminLoginFrame login = new AdminLoginFrame(App::openDashboard);
            login.setVisible(true);
        });
    }

    private static void openDashboard() {
        // Build application services only after successful admin authentication.
        StudentManager studentManager = new StudentManager();
        SeatAllocationUI seatAllocationUI = new SeatAllocationUI();

        AllocationService allocationService = new OperationsDAO();
        OperationStack operationStack = new OperationStack();
        OperationHistoryManager historyManager = new OperationHistoryManager();
        SeatOperationManager operationManager = new SeatOperationManager(
                allocationService, operationStack, historyManager);
        OperationsUI operationsUI = new OperationsUI(operationManager);

        ReportGenerator reportGenerator = new ReportGenerator(historyManager);
        ReportsUI reportsUI = new ReportsUI(reportGenerator);

        MainDashboard dashboard = new MainDashboard(
                studentManager, seatAllocationUI, operationsUI, reportsUI);
        dashboard.setVisible(true);
    }
}
