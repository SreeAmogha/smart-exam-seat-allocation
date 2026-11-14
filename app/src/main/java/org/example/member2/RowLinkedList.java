
package org.example.member2;

public class RowLinkedList {

    RowNode head;
    RowNode tail;
    int size = 0;
    int seatsPerRow = -1;

    public RowLinkedList() {
    }

    public void addRow(int rowNumber, int numberOfSeats) {

        if (seatsPerRow == -1) {
            seatsPerRow = numberOfSeats;
        }

        if (numberOfSeats != seatsPerRow) {
            System.out.println(
                "Error: Every row must have the same number of seats."
            );
            return;
        }

        RowNode newRow = new RowNode(rowNumber, seatsPerRow);

        if (head == null) {
            head = newRow;
            tail = newRow;
        } else {
            tail.next = newRow;
            newRow.prev = tail;
            tail = newRow;
        }

        size++;
    }

    public void displayRows() {
        RowNode current = head;

        while (current != null) {
            System.out.print("Row " + current.rowNumber + ": ");
            current.seats.displayForward();
            current = current.next;
        }
    }

    public void displayRowsBackward() {
        RowNode current = tail;

        while (current != null) {
            System.out.println("Row " + current.rowNumber);
            current = current.prev;
        }
    }

    public int getTotalCapacity() {
        int capacity = 0;
        RowNode current = head;

        while (current != null) {
            capacity += current.seats.getSize();
            current = current.next;
        }

        return capacity;
    }

    public int getRowCount() {
        return size;
    }

    public RowNode getHead() {
        return head;
    }

    public RowNode getTail() {
        return tail;
    }
}
