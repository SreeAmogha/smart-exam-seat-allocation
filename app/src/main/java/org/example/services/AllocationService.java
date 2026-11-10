package org.example.services;
import org.example.models.Operation;
import org.example.models.OperationResult;
import org.example.models.SeatLocation;
public interface AllocationService{
    OperationResult swapAllocations(int examId,String usn1,String usn2);
    OperationResult reassignAllocation(int examId,String usn,int hallId,int seatId);
    boolean restoreSwap(Operation operation);
    boolean restoreReassignment(Operation operation);
    SeatLocation getCurrentSeat(int eamId,String usn);
}