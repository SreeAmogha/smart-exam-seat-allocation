package org.example.member3;
import org.example.models.Operation;
import org.example.services.AllocationService;


public class UndoManager{
    //implementing Undo using already created operationStacks
    private OperationStack operationStack;
    private AllocationService allocationService;
    public UndoManager(OperationStack operationStack, AllocationService allocationService){
        this.operationStack=operationStack;
        this.allocationService=allocationService;
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
    public boolean undoLastOperation(){
        if(!canUndo()){
            return false;
        }
        Operation operation=operationStack.peek();
        boolean success=false;
        switch(operation.getOperationType()){
            case "SWAP":
                success=allocationService.restoreSwap(operation);
                break;
            case "REASSIGNMENT":
                success=allocationService.restoreReassignment(operation);
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