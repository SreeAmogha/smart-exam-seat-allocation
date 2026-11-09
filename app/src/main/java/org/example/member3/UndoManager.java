package org.example.member3;
import org.example.models.Operation;

public class UndoManager{
    //implementing Undo using already created operationStacks
    private OperationStack operationStack;
    public UndoManager(OperationStack operationStack){
        this.operationStack=operationStack;
    }
    public boolean canUndo(){
        return !operationStack.isEmpty();
    }
    public int getUndoCount(){
        return operationStack.size();
    }
    public Operation getLastOperation(){
        return operationStack.peek();
    }
    //reverses latest operation
    public boolean undoLastOperation(UndoActionHandler handler){
        if(!canUndo()){
            return false;
        }
        Operation operation=operationStack.peek();
        boolean success=false;
        switch(operation.getOperationType()){
            case "SWAP":
                success=handler.undoSwap(operation);
                break;
            case "REASSIGNMENT":
                success=handler.undoReassignment(operation);
                break;
            default:
                return false;
        }
        if(success){
            operationStack.pop();
            return true;
        }
        return false;
    }
}