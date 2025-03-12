package lab04;
import java.util.*;
public class RoundRobin extends SchedulingAlgorithm{
    private int quantum;
    private LinkedList<PCB> queue;

    public RoundRobin(List<PCB> processes, int quantum){
        super("Round Robin", processes);
        this.quantum = quantum;
        this.queue = new LinkedList<>(processes);
    }

    @Override
    public void schedule(){
        int execTime;
        System.out.println("Scheduling Algorithm: " + name);
        while(!queue.isEmpty()){
            PCB process = queue.poll();

            if(process.getStartTime() < 0)
                process.setStartTime(systemTime);
            
            execTime = Math.min(quantum, process.getCpuBurst()[0]);
            CPU.execute(process, execTime);
            systemTime += execTime;

            for(PCB proc: queue)
                proc.increaseWaitingTime(execTime);
            
            if(process.getCpuBurst()[0] > 0)
                queue.add(process);
            else{
                process.setFinishTime(systemTime);
                finishedProcs.add(process);
            }
        }
    }

    @Override
    public PCB pickNextProcess(){
        return queue.peek();
    }

}
