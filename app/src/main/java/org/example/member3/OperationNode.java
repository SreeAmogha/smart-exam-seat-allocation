package org.example.member3;
import org.example.models.Operation;
//Stack using linked list.
//OperationNode- node of linked list
public class OperationNode{
    Operation data;
    OperationNode next;
    public OperationNode(Operation data) {
        this.data=data;
        this.next=null;
    }
    
}