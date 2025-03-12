package lab04;

import java.util.*;

public class VirtualIO {
    private Queue<PCB> ioQueue; // FIFO holder for io opperations
    private PCB currentProcess; // holds the current process being run
    public int remainingIOTime; // holds the remaining time for io operations. may need to be private idk

    public VirtualIO() {
        this.ioQueue = new LinkedList<>();
        this.currentProcess = null;
        this.remainingIOTime = 0;
    }

    public void addProcess(PCB process, int time) { //adding procsess to the queue. starts one if the idle
        process.setWaitingTime(0);
        ioQueue.add(process);
        if (currentProcess == null)
            startNew();
    }

    private void startNew() { //starting a new process if there is one avalible
        if (!ioQueue.isEmpty()) {
            currentProcess = ioQueue.poll();
            remainingIOTime = currentProcess.getCpuBurst()[0]; //set burst time.
        }
    }

    public void executeIO(){ // executes the io operation
        if(currentProcess != null){
            remainingIOTime--;
            if(remainingIOTime == 0){
                currentProcess = null;
                startNew();
            }
        }
    }

    public boolean isIdle(){ //public getter for status
        return currentProcess == null;
    }
}
