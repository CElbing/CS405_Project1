package lab04;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public abstract class SchedulingAlgorithm {
	protected String name; // scheduling algorithm name
	protected List<PCB> allProcs; // the initial list of processes
	protected List<PCB> readyQueue; // ready queue of ready processes
	// protected List<PCB> waitQueue;
	protected List<PCB> finishedProcs; // list of terminated processes
	protected VirtualIO vIO;
	protected static PCB curProcess; // current selected process by the scheduler
	protected static PCB prevProcess; // previous process ran by the scheduler
	protected static int systemTime; // system time or simulation time steps
	protected boolean manualMode = false;
	protected int quantum;

	protected boolean enableLogging = true;
	protected PrintWriter logWriter;

	protected double avgTAT = 0.0;
	protected int totalTAT = 0;
	protected double avgWT = 0.0;
	protected int totalWT = 0;
	protected double cpuUtilization = 0.0;
	protected double throughput = 0.0;
	protected int cpuIdleTime = 0;
	protected long responseTime;

	protected Scanner sc = new Scanner(System.in);

	public SchedulingAlgorithm(String name, List<PCB> queue) throws FileNotFoundException {
		this.name = name;
		this.allProcs = queue;
		this.readyQueue = new ArrayList<>();
		this.finishedProcs = new ArrayList<>();
		// this.waitQueue = new ArrayList<>();
		this.vIO = new VirtualIO();
		// Writes all events to simulationResults.txt
		this.logWriter = enableLogging ? new PrintWriter("simulationResults.txt") : null;
	}

	public void setManualMode(boolean mode) {
		this.manualMode = mode;
	}

	public void setQuantum(int quantum){
		this.quantum = quantum;
	}

	@SuppressWarnings("resource")
	public void schedule() {
		long startResponseTime = System.currentTimeMillis();
		// - Print the name of the scheduling algorithm
		System.out.println("Scheduling algorithm: " + name);
		logWriter.println("Scheduling algorithm: " + name);
		logWriter.println(LocalDateTime.now());

		// - while (allProcs is not empty or readyQueue is not empty) {
		while (!allProcs.isEmpty() || !readyQueue.isEmpty() || !vIO.isIdle() || !vIO.getFinishedQueue().isEmpty()) {
			// - Print the current system time
			System.out.println("System time: " + systemTime);
			// - Move arrived processes from allProcs to readyQueue (arrivalTime =
			// systemTime)
			for (PCB proc : allProcs) {
				if (proc.getState().equals("")) {
					proc.setState("NEW");
					System.out.println("New process created: " + proc);
					logWriter.println();
					logWriter.println("New process created: " + proc + "\nSystem time: " + systemTime);
				}
				if (proc.getArrivalTime() == systemTime) {
					readyQueue.add(proc);
					proc.setState("READY");
					System.out.println("Process added to ready queue: " + proc);
					logWriter.println();
					logWriter.println("Process added to ready queue: " + proc + "\nSystem time: " + systemTime);
				}
			}
			allProcs.removeAll(readyQueue);
			// - curProcess = pickNextProcess() //call pickNextProcess() to choose next
			// process

			if (!readyQueue.isEmpty()) {
				prevProcess = curProcess;
				curProcess = pickNextProcess();
			} 
			else{
				cpuIdleTime += 1;
			}

			if (vIO.availableProcs() == true) {
				PCB ioProc = vIO.getReadyProcess();
				if (!finishedProcs.contains(ioProc) && ioProc.getBurst() > 0) {
					readyQueue.add(ioProc);
					ioProc.setState("READY");
					vIO.getFinishedQueue().remove(ioProc);
					System.out.println("Process completed IO returning to ready queue: " + ioProc);
					logWriter.println();
					logWriter.println("Process completed IO returning to ready queue: " + ioProc + "\nSystem time: "
							+ systemTime);
				}
			}

			for (PCB proc : vIO.getQueue()) {
				if (proc != vIO.getReadyProcess()) {
					proc.increaseIOWaitingTime(1);
				}
			}
			// - call print() to print simulation events: CPU, ready queue, ..
			print();
			// - update the start time of the selected process (curProcess)
			if (curProcess != null) {
				if (curProcess.getStartTime() < 0)
					curProcess.setStartTime(systemTime);
			}
			// - Call CPU.execute() to let the CPU execute 1 CPU unit time of curProcess
			if (!vIO.isIdle()) {
				VirtualIO.executeIO();
			}

			if (curProcess != null) {
				if (!readyQueue.isEmpty()) {
					if(!curProcess.getState().equals("RUNNING")){
						System.out.println("Process dispatching to CPU: " + curProcess);
						logWriter.println();
						logWriter.println("Process dispatching to CPU: " + curProcess + "\nSystem time: " + systemTime);
					}
					
					CPU.execute(curProcess, 1);
				}
			}

			// - Increase 1 to the waiting time of other processes in the ready queue
			for (PCB proc : readyQueue)
				if (proc != curProcess)
					proc.increaseWaitingTime(1);
			// - Increase systemTime by 1
			systemTime += 1;
			// - Check if the remaining CPU burst of curProcess = 0
			if (curProcess != null) {
				if (curProcess.getBurst() == 0) {
					if (curProcess.getIndex() < curProcess.getBurstArray().size() - 1) {
						curProcess.setIndex(curProcess.getIndex() + 1);
						readyQueue.remove(curProcess);
						vIO.addProcess(curProcess);
						System.out.println("Process entering IO: " + curProcess);
						logWriter.println();
						logWriter.println("Process entering IO: " + curProcess + "\nSystem time: " + systemTime);
						curProcess = null;
						if(name.equals("RR")){
							RR.setCount(0);
						}
					} else {
						curProcess.setFinishTime(systemTime);
						// Add to finished procs
						curProcess.setState("TERMINATED");
						finishedProcs.add(curProcess);
						readyQueue.remove(curProcess);
						String curProcessMetric = curProcess + "\nMetrics \n-------------- \nTAT: "
								+ curProcess.getTurnaroundTime() +
								" \nCPU WT: " + curProcess.getWaitingTime() +
								" \nIO WT:" + curProcess.getIOWaitingTime() +
								" \nFinish Time: " + curProcess.getFinishTime();
						System.out.println("Process terminated: " + curProcessMetric);
						logWriter.println();
						logWriter.println("Process terminated: " + curProcessMetric);
						System.out.println();
						totalWT += curProcess.getWaitingTime();
						totalTAT += curProcess.getTurnaroundTime();
						curProcess = null;
					}
				}
			}

			if (manualMode == true) {
				System.out.println("Press Enter to continue...");
				new Scanner(System.in).nextLine();
			}
		}
		print();
		System.out.println("All processes have been completed.");
		System.out.println("");

		avgTAT = (double) totalTAT/finishedProcs.size();
		avgWT = (double) totalWT/finishedProcs.size();
		throughput = (double) finishedProcs.size()/systemTime;
		cpuUtilization = (double) (systemTime - cpuIdleTime)/systemTime;
		long endResponseTime = System.currentTimeMillis();
		responseTime = endResponseTime - startResponseTime;

		System.out.println("Simulation Metric Summary: \n-----------------");
		System.out.println("AVG TAT = " + avgTAT);
		System.out.println("AVG WT = " + avgWT);
		System.out.println("Throughput = " + throughput);
		System.out.println("CPU Utilization = " + cpuUtilization + "%");
		System.out.println("Response Time = " + responseTime);

		logWriter.println();
		logWriter.println("All processes have been completed. \nSystem time: " + systemTime);
		logWriter.println();
		logWriter.println("Simulation Metric Summary: \n-----------------");
		logWriter.println("AVG TAT = " + avgTAT);
		logWriter.println("AVG WT = " + avgWT);
		logWriter.println("Throughput = " + throughput);
		logWriter.println("CPU Utilization = " + cpuUtilization + "%");
		logWriter.println("Response Time = " + responseTime);
		File logFile = new File("simulationResults.txt");

		System.out.println();
		String saveResults = "";
		System.out.println("Would you like to save the execution logs and system performance metrics for this simulation? [Y/N]");
		System.out.print("");
		do{
			System.out.print("Please enter [Y/N]: ");
			saveResults = sc.nextLine();
		}while(saveResults.equals("") || (!saveResults.equals("Y") && !saveResults.equals("N")));
		
		

		if(saveResults.equals("Y")){
			System.out.println("File succesfully saved as: " + logFile);
		}
		else{
			enableLogging = false;
		}
		logWriter.close();
	}

	// Selects the next task using the appropriate scheduling algorithm
	public abstract PCB pickNextProcess();

	// print simulation step
	public void print() {
		// add code to complete the method
		System.out.println();
		System.out.println("╔═╗╔═╗╦ ╦");
		System.out.println("║  ╠═╝║ ║");
		System.out.println("╚═╝╩  ╚═╝: " + ((curProcess == null) ? "idle" : curProcess.getName()) + "\n");
		for (PCB proc : readyQueue)
			System.out.println(proc);

		System.out.println();

		System.out.println("╦╔═╗");
		System.out.println("║║ ║");
		System.out.println("╩╚═╝: " + ((vIO.getProcess() == null) ? "idle" : vIO.getProcess().getName()) + "\n");
		for (PCB proc : vIO.getQueue())
			System.out.println(proc);

		System.out.println();
		System.out.println("<===========================================================================>");
		System.out.println();
	}

}
