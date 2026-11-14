
package org.example.services;

import org.example.models.Operation;
import org.example.models.OperationResult;
import org.example.models.SeatLocation;

public interface AllocationService {

    OperationResult swapAllocations(
            String examId, String usn1, String usn2);

    OperationResult reassignAllocation(
            String examId, String usn, String hallId, String seatId);

    boolean restoreSwap(Operation operation);

    boolean restoreReassignment(Operation operation);

    SeatLocation getCurrentSeat(String examId, String usn);
}
