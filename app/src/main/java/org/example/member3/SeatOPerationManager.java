package org.example.member3;

import org.example.models.Operation;
import org.example.models.OperationResult;
import org.example.models.SeatLocation;
import org.example.services.AllocationService;

public class SeatOperationManager{
    private AllocationService allocationService;
    private OperationStack operationStack;
    private UndoManager undoManager;
    private OperationHistoryManager historyManager;
    public SeatOperationManager(AllocationService allocationService,OperationStack operationStack,OperationHistoryManager historyManager){
        this.allocationService=allocationService;
        this.operationStack=operationStack;
        this.undoManager=new UndoManager(operationStack,allocationService);
        this.historyManager= historyManager;
    }
    public OperationResult swapSeats(int examId,String usn1,String usn2){
        if(examId<=0||usn1==null||usn2==null||usn1.isBlank()||usn2.isBlank()){
            return new OperationResult(false,"Invalid input");
        }
        if(usn1.equals(usn2)){
            return new OperationResult(false,"Cannot swap a student with themselves.");
        }
        SeatLocation seat1=allocationService.getCurrentSeat(examId, usn1);
        SeatLocation seat2=allocationService.getCurrentSeat(examId, usn2);
        if(seat1==null||seat2==null){
            return new OperationResult(false,"One or both students have no allocated seat.");
        }
        OperationResult result=allocationService.swapAllocations(examId,usn1,usn2);
        if(result.isSuccess()){
            Operation operation =new Operation(examId,"SWAP",usn1,usn2,seat1.getSeatId(),seat2.getSeatId(),seat2.getSeatId(),seat1.getSeatId());
            operationStack.push(operation);
            historyManager.addHistory(examId, "SWAP", usn1, usn2,seat1.getSeatId(), seat2.getSeatId(),seat2.getSeatId(), seat1.getSeatId());
        }
        return result;
    }
    public OperationResult reassignSeat(int examId,String usn,int hallId,int seatId){
        if(examId<=0||hallId<=0||seatId<=0||usn==null||usn.isBlank()){
            return new OperationResult(false,"Invalid input.");
        }
        SeatLocation oldSeat=allocationService.getCurrentSeat(examId,usn);
        if(oldSeat==null){
            return new OperationResult(false,"Student has no allocation seat.");
        }
        if(oldSeat.getSeatId()==seatId){
            return new OperationResult(false,"Student is already in this seat.");
        }
        OperationResult result=allocationService.reassignAllocation(examId, usn, hallId, seatId);
        if(result.isSuccess()){
            Operation operation=new Operation(examId,"REASSIGNMENT",usn,null,oldSeat.getSeatId(),-1,seatId,-1);
            operationStack.push(operation);
            historyManager.addHistory(examId, "REASSIGNMENT", usn, null, oldSeat.getSeatId(), -1, seatId, -1);
        }
        return result;
    }
    public SeatLocation getCurrentSeat(int examId,String usn){
        return allocationService.getCurrentSeat(examId, usn);
    }
    public boolean undoLastOperation(){
        Operation operation=undoManager.getLastOperation();
        if(operation==null){
            return false;
        }
        boolean success=undoManager.undoLastOperation();
        if(success){
            if(operation.getOperationType().equals("SWAP")){
                historyManager.addHistory(operation.getExamId(), "UNDO_SWAP", operation.getStudentUSN1(),operation.getStudentUSN2(), operation.getNewSeatId1(), operation.getNewSeatId2(), operation.getOldSeatId1(), operation.getOldSeatId2());
            }
            else if(operation.getOperationType().equals("REASSIGNMENT")){
                historyManager.addHistory(operation.getExamId(), "UNDO_REASSIGNMENT", operation.getStudentUSN1(),null, operation.getNewSeatId1(),-1, operation.getOldSeatId1(), -1);
            }
        }
        return success;
    }
    public int getUndoCount(){
        return undoManager.getUndoCount();
    }
    public Operation getLastOperation(){
        return undoManager.getLastOperation();
    }

}
