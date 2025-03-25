package lab04;

import java.io.FileNotFoundException;
import java.util.*;

public class RoundRobin extends SchedulingAlgorithm {
    private int quantum;
    private LinkedList<PCB> queue;

    public RoundRobin(List<PCB> processes, int quantum) throws FileNotFoundException {
        super("Round Robin", processes);
        this.quantum = quantum;
        this.queue = new LinkedList<>(processes);
    }

    @Override
    public void schedule() {
        int execTime;
        System.out.println("Scheduling Algorithm: " + name);
        while (!queue.isEmpty()) {
            PCB process = queue.peek();

            execTime = Math.min(quantum, process.getBurst());
            CPU.execute(process, execTime);
            systemTime += execTime;

            for (PCB proc : queue)
                proc.increaseWaitingTime(execTime);

            if (process.getBurst() > 0)
                queue.add(process);
            else {
                process.setFinishTime(systemTime);
                finishedProcs.add(process);
                queue.remove(process);
            }
        }
    }

    @Override
    public PCB pickNextProcess() {
        return queue.peek();
    }

}
