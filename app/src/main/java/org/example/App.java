
package org.example;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

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

        HallDAO hallDAO = new HallDAO();

        if (!hallDAO.testConnection()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Could not connect to Supabase. Check your database settings.",
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        SwingUtilities.invokeLater(() -> {

            // Student management
            StudentManager studentManager = new StudentManager();

            // Seat allocation
            SeatAllocationUI seatAllocationUI =
                    new SeatAllocationUI();

            // Operations
            AllocationService allocationService =
                    new OperationsDAO();

            OperationStack operationStack =
                    new OperationStack();

            OperationHistoryManager historyManager =
                    new OperationHistoryManager();

            SeatOperationManager operationManager =
                    new SeatOperationManager(
                            allocationService,
                            operationStack,
                            historyManager
                    );

            OperationsUI operationsUI =
                    new OperationsUI(operationManager);

            // Reports
            ReportGenerator reportGenerator =
                    new ReportGenerator(historyManager);

            ReportsUI reportsUI =
                    new ReportsUI(reportGenerator);

            // Main dashboard
            MainDashboard dashboard = new MainDashboard(
                    studentManager,
                    seatAllocationUI,
                    operationsUI,
                    reportsUI
            );

            dashboard.setVisible(true);
        });
    }
}
