
package org.example;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.example.member2.HallDAO;
import org.example.member2.SeatAllocationUI;
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

            // Temporary service until the database-backed
            // Operations service is implemented.
            AllocationService allocationService =
                    new TestAllocationService();

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

    // TEMPORARY SERVICE FOR TESTING
    static class TestAllocationService implements AllocationService {

        @Override
        public OperationResult swapAllocations(
                String examId, String usn1, String usn2) {
            return new OperationResult(
                    true, "Swap successful! (Test mode)");
        }

        @Override
        public OperationResult reassignAllocation(
                String examId, String usn, String hallId, String seatId) {
            return new OperationResult(
                    true, "Reassignment successful! (Test mode)");
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
        public SeatLocation getCurrentSeat(
                String examId, String usn) {

            if (usn == null) {
                return null;
            }

            switch (usn.toUpperCase()) {
                case "USN001":
                    return new SeatLocation(
                            "HALL_TEST_01", 1, 1,
                            "HALL_TEST_01_R1_S1");

                case "USN002":
                    return new SeatLocation(
                            "HALL_TEST_01", 1, 2,
                            "HALL_TEST_01_R1_S2");

                case "USN003":
                    return new SeatLocation(
                            "HALL_TEST_01", 1, 3,
                            "HALL_TEST_01_R1_S3");

                default:
                    return null;
            }
        }
    }
}
