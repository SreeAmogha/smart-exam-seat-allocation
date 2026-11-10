package org.example;
import org.example.member3.OperationHistoryManager;
import org.example.member3.OperationStack;
import org.example.member3.ReportGenerator;
import org.example.member3.UndoManager;
import org.example.models.Operation;
import org.example.models.OperationResult;
import org.example.models.SeatLocation;
import org.example.services.AllocationService;

public class App {

static class TestAllocationService implements AllocationService {
    public OperationResult swapAllocations(
            int examId, String usn1, String usn2) {
        return new OperationResult(true, "Swap successful");
    }
public OperationResult reassignAllocation(
        int examId, String usn, int hallId, int seatId) {
    return new OperationResult(true, "Reassignment successful");
}

    public boolean restoreSwap(Operation operation) {
        System.out.println("Attempting to undo swap...");
        return false;
    }

    public boolean restoreReassignment(Operation operation) {
        System.out.println("Attempting to undo reassignment...");
        return true;
    }

    public SeatLocation getCurrentSeat(int examId, String usn) {
        return null;
    }
}
    public static void main(String[] args) {

        OperationStack stack = new OperationStack();

        stack.push(new Operation(
            101,"SWAP", "USN001", "USN002",
            1, 5, 5, 1
        ));

        stack.push(new Operation(
            101,"REASSIGNMENT", "USN003", null,
            3, -1, 8, -1
        ));

AllocationService allocationService = new TestAllocationService();

UndoManager undoManager = new UndoManager(stack, allocationService);

        System.out.println("Undo count: " + undoManager.getUndoCount());

        System.out.println("Undo successful: "
                + undoManager.undoLastOperation());

        System.out.println("Undo count after undo: "
                + undoManager.getUndoCount());

        System.out.println("Next operation: "
                + undoManager.getLastOperation().getOperationType());
    

    OperationHistoryManager historyManager = new OperationHistoryManager();

historyManager.addHistory(
    101, "SWAP", "USN001", "USN002",
    1, 2, 2, 1
);

historyManager.addHistory(
    101, "REASSIGNMENT", "USN003", null,
    3, -1, 8, -1
);

historyManager.addHistory(
    102, "REASSIGNMENT", "USN001", null,
    5, -1, 9, -1
);

System.out.println("Total history: " + historyManager.getAllHistory().size());

System.out.println("Exam 101 history: "
    + historyManager.getHistoryByExam(101).size());

System.out.println("USN001 history: "
    + historyManager.getHistoryByStudent("USN001").size());
ReportGenerator reportGenerator = new ReportGenerator(historyManager);

System.out.println("Exam 101 total operations: "
        + reportGenerator.getTotalOperations(101));

System.out.println("Exam 101 swaps: "
        + reportGenerator.getTotalSwaps(101));

System.out.println("Exam 101 reassignments: "
        + reportGenerator.getTotalReassignments(101));

System.out.println("Exam 101 undo operations: "
        + reportGenerator.getTotalUndoOperations(101));

System.out.println("USN001 total operations: "
        + reportGenerator.getTotalOperationsByStudent("USN001"));

System.out.println("USN001 swaps: "
        + reportGenerator.getSwapsByStudent("USN001"));

System.out.println("USN001 reassignments: "
        + reportGenerator.getReassignmentsByStudent("USN001"));
}
}