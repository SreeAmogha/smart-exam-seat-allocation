package org.example;
import org.example.member3.OperationStack;
import org.example.member3.UndoActionHandler;
import org.example.member3.UndoManager;
import org.example.models.Operation;

public class App {

    // Temporary handler for testing
    static class TestHandler implements UndoActionHandler {

        public boolean undoSwap(Operation operation) {
            System.out.println("Undoing swap...");
            return false;
        }

        public boolean undoReassignment(Operation operation) {
            System.out.println("Undoing reassignment...");
            return false;
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

        UndoManager undoManager = new UndoManager(stack);

        TestHandler handler = new TestHandler();

        System.out.println("Undo count: " + undoManager.getUndoCount());

        System.out.println("Undo successful: "
                + undoManager.undoLastOperation(handler));

        System.out.println("Undo count after undo: "
                + undoManager.getUndoCount());

        System.out.println("Next operation: "
                + undoManager.getLastOperation().getOperationType());
    }
}
