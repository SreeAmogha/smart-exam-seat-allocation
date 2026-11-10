package org.example.member3;
import java.util.List;

import org.example.models.AllocationHistory;

public class ReportGenerator{
    private OperationHistoryManager historyManager;
    public ReportGenerator(OperationHistoryManager historyManager){
        this.historyManager=historyManager;
    }
    public int getTotalOperations(int examId){
        return historyManager.getHistoryByExam(examId).size();
    }
    public int getTotalSwaps(int examId){
        int count=0;
        List<AllocationHistory>history=historyManager.getHistoryByExam(examId);
        for(AllocationHistory record:history){
            if(record.getOperationType().equals("SWAP")){
                count++;
            }
        }
        return count;
    }
    public  int getTotalReassignments(int examId){
        int count=0;
        List<AllocationHistory>history =historyManager.getHistoryByExam(examId);
        for(AllocationHistory record:history){
            if(record.getOperationType().equals("REASSIGNMENT")){
                count++;
            }
        }
        return count;
    }
    public int getTotalUndoOperations(int examId){
        int count=0;
        List<AllocationHistory>history=historyManager.getHistoryByExam(examId);
        for(AllocationHistory record:history){
            if(record.getOperationType().equals("REASSIGNMENT")||record.getOperationType().equals("SWAP")){
                count++;
            }
        }
        return count;
    }
    public int getTotalOperationsByStudent(String usn){
        return historyManager.getHistoryByStudent(usn).size();
    }
    public int getSwapsByStudent(String usn){
        int count=0;
        List<AllocationHistory>history=historyManager.getHistoryByStudent(usn);
        for(AllocationHistory record:history){
            if(record.getOperationType().equals("SWAP")){
                count++;
            }
        }
        return count;
    }
    public int getReassignmentsByStudent(String usn){
        int count=0;
        List<AllocationHistory>history=historyManager.getHistoryByStudent(usn);
        for(AllocationHistory record:history){
            if(record.getOperationType().equals("REASSIGNMENT")){
                count++;
            }
        }
        return count;
    }
    public List<AllocationHistory> getStudentHistory(String usn) {
        return historyManager.getHistoryByStudent(usn);
    }
    public List<AllocationHistory> getExamHistory(int examId) {
        return historyManager.getHistoryByExam(examId);
    }

}