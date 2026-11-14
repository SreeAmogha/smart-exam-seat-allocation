
package org.example;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

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

            // Real database-backed Operations service
            AllocationService allocationService =
                    new OperationsDAO();

            OperationStack operationStack = new OperationStack();

            OperationHistoryManager historyManager =
                    new OperationHistoryManager();

            SeatOperationManager operationManager =
                    new SeatOperationManager(
                            allocationService,
                            operationStack,
                            historyManager
                    );

            ReportGenerator reportGenerator =
                    new ReportGenerator(historyManager);

            new SeatAllocationUI().setVisible(true);
            new OperationsUI(operationManager);
            new ReportsUI(reportGenerator);
        });
    }
}
