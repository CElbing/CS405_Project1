package lab04;

import java.util.*;

public abstract class SchedulingAlgorithm {
	protected String name; // scheduling algorithm name
	protected List<PCB> allProcs; // the initial list of processes
	protected List<PCB> readyQueue; // ready queue of ready processes
	// protected List<PCB> waitQueue;
	protected List<PCB> finishedProcs; // list of terminated processes
	protected VirtualIO vIO;
	protected PCB curProcess; // current selected process by the scheduler
	protected static int systemTime; // system time or simulation time steps
	protected boolean manualMode = false;

	public SchedulingAlgorithm(String name, List<PCB> queue) {
		this.name = name;
		this.allProcs = queue;
		this.readyQueue = new ArrayList<>();
		this.finishedProcs = new ArrayList<>();
		// this.waitQueue = new ArrayList<>();
		this.vIO = new VirtualIO();
	}

	public void setManualMode(boolean mode) {
		this.manualMode = mode;
	}

	public static int getSystemTime() {
		return systemTime;
	}

	@SuppressWarnings("resource")
	public void schedule() {
		// - Print the name of the scheduling algorithm
		System.out.println("Scheduling algorithm: " + name);
		// - while (allProcs is not empty or readyQueue is not empty) {
		while (!allProcs.isEmpty() || !readyQueue.isEmpty() || !vIO.isIdle()) {
			// - Print the current system time
			System.out.println("System time: " + systemTime);
			// - Move arrived processes from allProcs to readyQueue (arrivalTime =
			// systemTime)
			for (PCB proc : allProcs) {
				if (proc.getArrivalTime() >= systemTime)
					readyQueue.add(proc);
			}
			allProcs.removeAll(readyQueue);
			// - curProcess = pickNextProcess() //call pickNextProcess() to choose next
			// process
			if (!readyQueue.isEmpty()) {
				curProcess = pickNextProcess();
			}
			if(vIO.availableProcs() == true){
				PCB ioProc = vIO.getReadyProcess();
			    if (!finishedProcs.contains(ioProc) && ioProc.getBurst() > 0) {
			        readyQueue.add(ioProc);
			    }
			}
			// - call print() to print simulation events: CPU, ready queue, ..
			print();
			// - update the start time of the selected process (curProcess)
			if (curProcess.getStartTime() < 0)
				curProcess.setStartTime(systemTime);
			// - Call CPU.execute() to let the CPU execute 1 CPU unit time of curProcess
			if (!vIO.isIdle()) {
				vIO.executeIO();
			}
			if (curProcess.getIndex() % 2 == 0) {
				CPU.execute(curProcess, 1);
			}
			if(readyQueue.isEmpty()) {
				curProcess = null;
			}
			// - Increase 1 to the waiting time of other processes in the ready queue
			for (PCB proc : readyQueue)
				if (proc != curProcess)
					proc.increaseWaitingTime(1);
			// - Increase systemTime by 1
			systemTime += 1;
			// - Check if the remaining CPU burst of curProcess = 0
			if (curProcess.getBurst() <= 0) {
				if (curProcess.getIndex() < curProcess.getBurstArray().size()-1) {
					curProcess.setIndex(curProcess.getIndex() + 1);
					readyQueue.remove(curProcess);
					vIO.addProcess(curProcess);
				}
				else{
					curProcess.setFinishTime(systemTime);
					// Add to finished procs
					finishedProcs.add(curProcess);
					readyQueue.remove(curProcess);
					System.out.println("Process " + curProcess.getId() + " is complete");
				}
			}

			if (manualMode == true) {
				System.out.println("Press Enter to continue...");
				new Scanner(System.in).nextLine();
			}
		}
	}

	// Selects the next task using the appropriate scheduling algorithm
	public abstract PCB pickNextProcess();

	// print simulation step
	public void print() {
		// add code to complete the method
		System.out.println("CPU " + ((curProcess == null) ? "idle" : curProcess.getName()));
			for (PCB proc : readyQueue)
				System.out.println(proc);
			
		System.out.println("IO " + ((vIO.getProcess() == null) ? "idle" : vIO.getProcess().getName()));
			for (PCB proc : vIO.getQueue()) {
				System.out.println(proc);
		}
	}
}
