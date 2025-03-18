package lab04;
import java.util.*;

public class VirtualIO {
    private Queue<PCB> ioQueue; // FIFO holder for io opperations
    private PCB currentProcess; // holds the current process being run
    private Queue<PCB> finishedQueue;
    //private int remainingIOTime; // holds the remaining time for io operations. may need to be public idk

    public VirtualIO() {
        this.ioQueue = new LinkedList<>();
        this.currentProcess = null;
        this.finishedQueue = new LinkedList<>();
        //this.remainingIOTime = 0;
    }

    public Queue<PCB> getQueue(){
        return ioQueue;
    }

    public void addProcess(PCB process) { //adding procsess to the queue. starts one if the idle
        process.setWaitingTime(0);
        ioQueue.add(process);
        //this.remainingIOTime = time;
        if (currentProcess == null)
            startNew();
    }

    public PCB getProcess() {
        return ioQueue.peek();
    }

    public PCB getReadyProcess(){
        PCB temp = finishedQueue.peek();
        finishedQueue.remove();
        return temp;
    }

    public boolean availableProcs(){
        return finishedQueue.size() > 0;
    }

    private void startNew() { //starting a new process if there is one avalible
        if (!ioQueue.isEmpty()) {
            //currentProcess = ioQueue.poll();
            currentProcess = ioQueue.peek();
            //remainingIOTime = currentProcess.getCpuBurst()[0]; //set burst time.
        }
    }

    public void executeIO(){ // executes the io operation
        if(currentProcess != null){
            currentProcess.setBurst(currentProcess.getBurst() - 1);
            if(currentProcess.getBurst() == 0){
                //currentProcess = null;
                currentProcess.setIndex(currentProcess.getIndex() + 1);
                finishedQueue.add(currentProcess);
                ioQueue.remove(currentProcess);
                startNew();
            }
        }
    }

    public boolean isIdle(){ //public getter for status
        return currentProcess == null;
    }

}
