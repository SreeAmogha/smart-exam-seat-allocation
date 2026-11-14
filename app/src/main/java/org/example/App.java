
package org.example;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.example.member2.HallDAO;
import org.example.member3.OperationHistoryManager;
import org.example.member3.OperationStack;
import org.example.member3.OperationsUI;
import org.example.member3.ReportGenerator;
import org.example.member3.ReportsUI;
import org.example.member3.SeatOperationManager;
import org.example.models.Operation;
import org.example.models.OperationResult;
import org.example.models.SeatLocation;
import org.example.services.AllocationService;

public class App {

    public static void main(String[] args) {

        // Check database connection
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

            // Temporary test service
            AllocationService allocationService =
                    new TestAllocationService();

            // Create operation stack and history manager
            OperationStack operationStack = new OperationStack();

            OperationHistoryManager historyManager =
                    new OperationHistoryManager();

            // Create operation manager
            SeatOperationManager operationManager =
                    new SeatOperationManager(
                            allocationService,
                            operationStack,
                            historyManager
                    );

            // Create report generator
            ReportGenerator reportGenerator =
                    new ReportGenerator(historyManager);

            // Open both GUIs
            new OperationsUI(operationManager);
            new ReportsUI(reportGenerator);
        });
    }

    // TEMPORARY SERVICE FOR TESTING
    static class TestAllocationService implements AllocationService {

        @Override
        public OperationResult swapAllocations(
                int examId, String usn1, String usn2) {

            return new OperationResult(
                    true, "Swap successful! (Test mode)"
            );
        }

        @Override
        public OperationResult reassignAllocation(
                int examId, String usn, int hallId, int seatId) {

            return new OperationResult(
                    true, "Reassignment successful! (Test mode)"
            );
        }

        @Override
        public boolean restoreSwap(Operation operation) {
            return true;
        }

        @Override
        public boolean restoreReassignment(Operation operation) {
            return true;
        }

        @Override
        public SeatLocation getCurrentSeat(int examId, String usn) {

            if (usn == null) {
                return null;
            }

            switch (usn.toUpperCase()) {
                case "USN001":
                    return new SeatLocation(1, 1, 1, 1);

                case "USN002":
                    return new SeatLocation(1, 1, 2, 2);

                case "USN003":
                    return new SeatLocation(1, 1, 3, 3);

                default:
                    return null;
            }
        }
    }
}
