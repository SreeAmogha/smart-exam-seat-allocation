package org.example.member3;
import org.example.models.Operation;

//implementing stack using singly linked list
public class OperationStack{
    private OperationNode top;
    private int size;
    public OperationStack(){
        top=null;
        size=0;
    }
    public void push(Operation operation){
        OperationNode newNode=new OperationNode(operation);
        newNode.next=top;
        top=newNode;
        size++;
    }
    public Operation pop(){
        if(isEmpty()){
            return null;
        }
        Operation removed=top.data;
        top=top.next;
        size--;
        return removed;
    }
    public Operation peek(){
        if(isEmpty()){
            return null;
        }
        return top.data;
    }
    public boolean isEmpty(){
        return top==null;
    }
    public int size(){
        return size;
    }
    public void clear(){     //remove all operations
        top=null;
        size=0;
    }
}
