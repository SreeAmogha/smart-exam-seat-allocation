package org.example.member3;
import org.example.models.Operation;

public interface UndoActionHandler{
    boolean undoSwap(Operation operation);
    boolean undoReassignment(Operation operation);
}
