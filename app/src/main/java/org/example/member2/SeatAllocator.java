package org.example.member2;

import org.example.member1.Student;
import java.util.*;

public class SeatAllocator {
    private ConflictDetector conflictDetector;

    public SeatAllocator() {
        this.conflictDetector = new ConflictDetector();
    }

    public boolean allocateHall(Hall hall, List<Student> studentsToAllocate) {
        Queue<Student> studentQueue = new LinkedList<>(studentsToAllocate);
        Student previousStudent = null;

        // Traversal logic utilizing your existing RowLinkedList and DoublyLinkedSeatList structures
        RowNode currentRowNode = hall.getRows().getHead();
        while (currentRowNode != null) {
            DoublyLinkedSeatList seatList = currentRowNode.getSeatList();
            SeatNode currentSeatNode = seatList.getHead();

            while (currentSeatNode != null) {
                if (studentQueue.isEmpty()) {
                    return true; // All students allocated successfully
                }

                Student candidate = studentQueue.peek();

                // Check conflict with adjacent student
                if (previousStudent != null && conflictDetector.hasConflict(previousStudent, candidate)) {
                    studentQueue.poll();
                    studentQueue.offer(candidate); // Re-queue candidate to enforce separation
                    candidate = studentQueue.peek();
                }

                // Allocate student to current seat node
                currentSeatNode.setAssignedUsn(candidate.getUsn());
                previousStudent = candidate;
                studentQueue.poll();

                currentSeatNode = currentSeatNode.getNext();
            }
            currentRowNode = currentRowNode.getNext();
        }
        return studentQueue.isEmpty();
    }
}
