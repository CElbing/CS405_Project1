package lab04;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

// abstract class for scheduling algorithms to build from
// subclasses are FCFS, RR, SJF, and PriorityScheduling
public abstract class SchedulingAlgorithm {
	protected String name; // scheduling algorithm name
	protected List<PCB> allProcs; // the initial list of processes
	protected List<PCB> readyQueue; // ready queue of ready processes
	protected List<PCB> finishedProcs; // list of terminated processes
	protected VirtualIO vIO; // simulated IO device handler
	protected PCB curProcess; // current selected process by the scheduler
	protected PCB prevProcess; // previous process ran by the scheduler

	// tracking variables
	protected int systemTime; // system time or simulation time steps
	protected boolean manualMode = false; // manual mode flag
	protected boolean termSim = false; // termination flag
	protected int quantum; // quantum time for RR scheduling
	protected Scanner manualSc; // scanner for manual mode

	// logging variables
	protected boolean enableLogging = true; // enable logging flag
	protected PrintWriter logWriter;
	protected double avgTAT = 0.0; // average turnaround time
	protected int totalTAT = 0; // total turnaround time
	protected double avgWT = 0.0; // average waiting time
	protected int totalWT = 0; // total waiting time
	protected int totalIoWT = 0;
	protected double avgIoWT = 0.0;
	protected double cpuUtilization = 0.0; // ratio of CPU time to total time
	protected double throughput = 0.0; // completed processes per unit time
	protected int cpuIdleTime = 0; // total time CPU was idle
	protected long responseTime; // total time to complete simulation
	protected Scanner sc = new Scanner(System.in); // user prompt scanner

	// constructor
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

	// setter methods
	public void setManualMode(boolean mode) {
		this.manualMode = mode;
	}

	public void setQuantum(int quantum) {
		this.quantum = quantum;
	}

	public void schedule() {

		// Print the name of the scheduling algorithm
		System.out.println("Scheduling algorithm: " + name);
		logWriter.println("Scheduling algorithm: " + name);
		logWriter.println(LocalDateTime.now());

		// main sim loop. Makes sure all processes are completed before ending, ready
		// queue is empty, IO is idle, and VIO finished queue is empty
		while (!allProcs.isEmpty() || !readyQueue.isEmpty() || !vIO.isIdle()) {
			if (termSim == true) { // checking termination flag status
				break;
			}
			// Print the current system time
			System.out.println("System time: " + systemTime);
			// Move arrived processes from allProcs to readyQueue (arrivalTime =
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
			// curProcess = pickNextProcess() //call pickNextProcess() to choose next
			// process

			// pick the next avaliable process
			if (!readyQueue.isEmpty()) {
				prevProcess = curProcess;
				curProcess = pickNextProcess();
			} else {
				cpuIdleTime += 1;
			}

			// handling fished IO processes returning to the ready queue
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
			// increment IO wait time for processes in the IO
			for (PCB proc : vIO.getQueue()) {
				if (proc != vIO.getReadyProcess()) {
					proc.increaseIOWaitingTime(1);
				}
			}

			// call print() to print simulation events: CPU, ready queue, ..
			print();
			// update the start time of the selected process (curProcess)
			if (curProcess != null) {
				if (curProcess.getStartTime() < 0)
					curProcess.setStartTime(systemTime);
			}

			// Call CPU.execute() to let the CPU execute 1 CPU unit time of curProcess
			if (!vIO.isIdle()) {
				VirtualIO.executeIO();
			}

			// if the current process is not null and the ready queue is not empty
			if (curProcess != null) {
				if (!readyQueue.isEmpty()) {
					// Switch state of process to running if it is newly dispatched process
					if (!curProcess.getState().equals("RUNNING")) {
						// Log and display dispatch event
						System.out.println("Process dispatching to CPU: " + curProcess);
						logWriter.println();
						logWriter.println("Process dispatching to CPU: " + curProcess + "\nSystem time: " + systemTime);
						// Calculate response time for first time process enters CPU
						if (curProcess.getRunningCount() == 0) {
							System.out.println("Response time: " + (systemTime - curProcess.getArrivalTime()));
							logWriter.print("Response time: " + (systemTime - curProcess.getArrivalTime()));
							curProcess.setRunningCount(1);
						}
					}
					CPU.execute(curProcess, 1);
				}
			}

			// Increase 1 to the waiting time of other processes in the ready queue
			for (PCB proc : readyQueue)
				if (proc != curProcess)
					proc.increaseWaitingTime(1);

			// Increase systemTime by 1
			systemTime += 1;

			// - Check if the remaining CPU burst of curProcess = 0
			if (curProcess != null) {
				if (curProcess.getBurst() == 0) {
					if (curProcess.getIndex() < curProcess.getBurstArray().size() - 1) { // check if there are more
																							// bursts remaining
						curProcess.setIndex(curProcess.getIndex() + 1); // mve to the next burst
						readyQueue.remove(curProcess);

						// remove the process from the ready queue and add it to the IO queue
						vIO.addProcess(curProcess);
						System.out.println("Process entering IO: " + curProcess);

						logWriter.println(); // logging
						logWriter.println("Process entering IO: " + curProcess + "\nSystem time: " + systemTime);

						curProcess = null; // clear the current process so the cpu can get a new one

						if (name.equals("RR")) { // if RR then reset the counter
							RR.setCount(0);
						}
					} else {
						// no more bursts remaining
						curProcess.setFinishTime(systemTime); // mark finish time
						curProcess.setState("TERMINATED"); // update state
						finishedProcs.add(curProcess); // add to the list of completed processes
						readyQueue.remove(curProcess); // renove from the ready queue

						// generate summary metrics for the process
						String curProcessMetric = curProcess + "\nMetrics \n-------------- \nTAT: "
								+ curProcess.getTurnaroundTime() +
								" \nCPU WT: " + curProcess.getWaitingTime() +
								" \nIO WT:" + curProcess.getIOWaitingTime() +
								" \nFinish Time: " + curProcess.getFinishTime();

						// Outout and log termincation and metics
						System.out.println("Process terminated: " + curProcessMetric);
						logWriter.println();
						logWriter.println("Process terminated: " + curProcessMetric);
						System.out.println();

						totalWT += curProcess.getWaitingTime(); // update total waiting time
						totalTAT += curProcess.getTurnaroundTime(); // update total turnaround time
						totalIoWT += curProcess.getIOWaitingTime(); // update total IO waiting time

						curProcess = null; // clear the CPU
					}
				}
			}

			if (manualMode == true) { // if manual mode is enabled pause after each line
				System.out.println("Press Enter to continue...");
				System.out.println("If you would like to terminate this simulation enter [X]");

				manualSc = new Scanner(System.in); // creating its own scanner to avoid conflicts
				if (manualSc.nextLine().equalsIgnoreCase("X")) {
					termSim = true;
				}
			}
		}

		// print final snapshot
		print();

		// calculate simulation metrics
		avgTAT = (double) totalTAT / finishedProcs.size(); // calculate average turnaround time
		avgWT = (double) totalWT / finishedProcs.size(); // calculate average waiting time
		avgIoWT = (double) totalIoWT/ finishedProcs.size(); // calculate average IO waiting time
		throughput = (double) finishedProcs.size() / systemTime; // calculate throughput
		cpuUtilization = (double) (systemTime - cpuIdleTime) / systemTime; // calculate CPU utilization

		// print blocks
		System.out.println("All processes have been terminated.");
		System.out.println("");
		System.out.println("Simulation Metric Summary: \n-----------------");
		System.out.println("AVG TAT = " + avgTAT);
		System.out.println("AVG WT = " + avgWT);
		System.out.println("AVG IO WT = " + avgIoWT);
		System.out.println("Throughput = " + throughput);
		System.out.println("CPU Utilization = " + cpuUtilization + "%");

		// log blocks
		logWriter.println();
		logWriter.println("All processes have been terminated. \nSystem time: " + systemTime);
		logWriter.println();
		logWriter.println("Simulation Metric Summary: \n-----------------");
		logWriter.println("AVG TAT = " + avgTAT);
		logWriter.println("AVG WT = " + avgWT);
		logWriter.println(("AVG IO WT = " + avgIoWT));
		logWriter.println("Throughput = " + throughput);
		logWriter.println("CPU Utilization = " + cpuUtilization + "%");
		File logFile = new File("simulationResults.txt");

		System.out.println();
		String saveResults = "";
		while(!saveResults.equals("Y") && !saveResults.equals("N")){
			System.out.println(
				"Would you like to save the execution logs and system performance metrics for this simulation? [Y/N]");
			saveResults = sc.nextLine();
		}

		if (saveResults.equalsIgnoreCase("Y")) {
			System.out.println("File succesfully saved as: " + logFile);
		} else {
			enableLogging = false; // disable logging
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
