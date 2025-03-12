package lab04;
import java.util.*;
public abstract class SchedulingAlgorithm {
    protected String name;		      //scheduling algorithm name
	protected List<PCB> allProcs;		//the initial list of processes
	protected List<PCB> readyQueue;	//ready queue of ready processes
	protected List<PCB> finishedProcs;	//list of terminated processes
	protected PCB curProcess; //current selected process by the scheduler
	protected int systemTime; //system time or simulation time steps
 
      public SchedulingAlgorithm(String name, List<PCB> queue) {
    	      this.name = name;
    	      this.allProcs = queue;
    	      this.readyQueue = new ArrayList<>();
    	      this.finishedProcs = new ArrayList<>();
      }
	
	public void schedule() {
		//- Print the name of the scheduling algorithm
		System.out.println("Scheduling algorithm: " + name);
		//- while (allProcs is not empty or readyQueue is not empty) {
		while(!allProcs.isEmpty() || !readyQueue.isEmpty()) {
		//    - Print the current system time
			System.out.println("System time: " + systemTime);
		//    - Move arrived processes from allProcs to readyQueue (arrivalTime = systemTime)
			for(PCB proc : allProcs) {
				if(proc.getArrivalTime() >= systemTime)
					readyQueue.add(proc);
			}
			allProcs.removeAll(readyQueue);
		//    - curProcess = pickNextProcess() //call pickNextProcess() to choose next process
			curProcess = pickNextProcess();
		//    - call print() to print simulation events: CPU, ready queue, ..
			print();
		//    - update the start time of the selected process (curProcess)
			if(curProcess.getStartTime() < 0)
				curProcess.setStartTime(systemTime);
		//    - Call CPU.execute() to let the CPU execute 1 CPU unit time of curProcess
			CPU.execute(curProcess, 1);
		//    - Increase 1 to the waiting time of other processes in the ready queue
			for(PCB proc: readyQueue)
				if(proc != curProcess) proc.increaseWaitingTime(1);
		//   - Increase systemTime by 1
			systemTime += 1;
		//    - Check if the remaining CPU burst of curProcess = 0
			if(curProcess.getCpuBurst()[0] == 0) {
		//        - Update finishTime of curProcess
				curProcess.setFinishTime(systemTime);
		//        - remove curProcess from readyQueue
				readyQueue.remove(curProcess);
		//        - add curProcess to the finished queue (finishedProcs)
				finishedProcs.add(curProcess);
		//        - Print to the console a message displaying the process name, terminated time, 
		//                                               startTime, turnaroundTime, waitingTime
				System.out.println("Procces ");
		//    - Print a new line
			}
		}
	
	}
	
	//Selects the next task using the appropriate scheduling algorithm
      public abstract PCB pickNextProcess();

      //print simulation step
      public void print() {
		//  add code to complete the method	
    	  System.out.println("CPU " + ((curProcess == null) ? "idle" : curProcess.getName()));
    	  for(PCB proc: readyQueue)
    		  System.out.println(proc);
      }
}
