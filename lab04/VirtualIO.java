package lab04;

import java.util.*;

public class VirtualIO {
    private static Queue<PCB> ioQueue; // FIFO holder for io opperations
    private static PCB currentProcess; // holds the current process being run
    private static Queue<PCB> finishedQueue;
    // private int remainingIOTime; // holds the remaining time for io operations.
    // may need to be public idk

    public VirtualIO() {
        ioQueue = new LinkedList<>();
        currentProcess = null;
        finishedQueue = new LinkedList<>();
        // this.remainingIOTime = 0;
    }

    public Queue<PCB> getQueue() {
        return ioQueue;
    }

    public void setProcess(PCB process) {
        currentProcess = process;
    }

    public Queue<PCB> getFinishedQueue() {
        return finishedQueue;
    }

    public void addProcess(PCB process) { // adding procsess to the queue. starts one if the idle
        // process.setWaitingTime(0);
        ioQueue.add(process);
        process.setState("WAITING");
        // this.remainingIOTime = time;
    }

    public PCB getProcess() {
        return ioQueue.peek();
    }

    public PCB getReadyProcess() {
        return finishedQueue.peek();
    }

    public boolean availableProcs() {
        return finishedQueue.size() > 0;
    }

    private static void startNew() { // starting a new process if there is one avalible
        if (!ioQueue.isEmpty()) {
            // currentProcess = ioQueue.poll();
            currentProcess = ioQueue.peek();
            // remainingIOTime = currentProcess.getCpuBurst()[0]; //set burst time.
        }
    }

    public static void executeIO() { // executes the io operation
        if (currentProcess == null) {
            startNew();
        } else {
            currentProcess.setBurst(currentProcess.getBurst() - 1);
            if (currentProcess.getBurst() == 0) {
                // currentProcess = null;
                currentProcess.setIndex(currentProcess.getIndex() + 1);
                finishedQueue.add(currentProcess);
                ioQueue.remove(currentProcess);
                currentProcess = null;
                startNew();
            }
        }
    }

    public boolean isIdle() { // public getter for status
        return ioQueue.isEmpty();
    }
}
