
package org.example.member2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.example.member1.Student;

public class SeatAllocator {

    private ConflictDetector conflictDetector;
    private List<Student> unallocatedStudents;

    public SeatAllocator() {
        conflictDetector = new ConflictDetector();
        unallocatedStudents = new ArrayList<>();
    }

    public boolean allocateHall(Hall hall, List<Student> students) {

        unallocatedStudents.clear();

        allocatedStudents.clear();

        if (hall == null || hall.getRows() == null || students == null) {
            return false;
        }

        Queue<Student> queue = new LinkedList<>(students);

        RowNode row = hall.getRows().getHead();

        while (row != null) {

            SeatNode seat = row.getSeatList().getHead();

            while (seat != null && !queue.isEmpty()) {

                if (seat.isOccupied()) {
                    seat = seat.getNext();
                    continue;
                }

                int candidates = queue.size();
                Student selected = null;

                for (int i = 0; i < candidates; i++) {

                    Student candidate = queue.poll();

                    if (isSafePosition(hall, seat, candidate)) {
                        selected = candidate;
                        break;
                    }

                    queue.offer(candidate);
                }

                if (selected != null) {
    seat.setAssignedUsn(selected.getUsn());
    allocatedStudents.add(selected);
}

                seat = seat.getNext();
            }

            row = row.getNext();
        }

        unallocatedStudents.addAll(queue);

        return unallocatedStudents.isEmpty();
    }

    private boolean isSafePosition(
            Hall hall, SeatNode seat, Student candidate) {

        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            { 0, -1},           { 0, 1},
            { 1, -1}, { 1, 0}, { 1, 1}
        };

        for (int[] direction : directions) {

            int neighbourRow = seat.getRowNumber() + direction[0];
            int neighbourSeat = seat.getSeatNumber() + direction[1];

            SeatNode neighbour =
                findSeat(hall, neighbourRow, neighbourSeat);

            if (neighbour != null && neighbour.isOccupied()) {

                Student neighbourStudent =
                    findStudentByUsn(neighbour.getAllocatedUsn());

                if (neighbourStudent != null &&
                    conflictDetector.hasConflict(candidate, neighbourStudent)) {
                    return false;
                }
            }
        }

        return true;
    }

    private SeatNode findSeat(
            Hall hall, int rowNumber, int seatNumber) {

        RowNode row = hall.getRows().getHead();

        while (row != null) {

            if (row.getRowNumber() == rowNumber) {

                SeatNode seat = row.getSeatList().getHead();

                while (seat != null) {

                    if (seat.getSeatNumber() == seatNumber) {
                        return seat;
                    }

                    seat = seat.getNext();
                }
            }

            row = row.getNext();
        }

        return null;
    }

    private Student findStudentByUsn(String usn) {

        for (Student student : allocatedStudents) {
            if (student.getUsn().equals(usn)) {
                return student;
            }
        }

        return null;
    }

    private List<Student> allocatedStudents = new ArrayList<>();

    public List<Student> getUnallocatedStudents() {
        return new ArrayList<>(unallocatedStudents);
    }
    public List<Student> allocateAcrossHalls(
        List<Hall> halls, List<Student> students) {

    if (halls == null || students == null) {
        return new ArrayList<>();
    }

    List<Student> remainingStudents = new ArrayList<>(students);

    for (Hall hall : halls) {

        if (remainingStudents.isEmpty()) {
            break;
        }

        allocateHall(hall, remainingStudents);

        remainingStudents = getUnallocatedStudents();
    }

    return remainingStudents;
}
}
