package lab04;
import java.util.*;

public class PCB {
	// the representation of each process
	private String name; // process name
	private int id; // process id
	private int totalCpuBurstTime = 0;
	private int totalIoBurstTime = 0;
	private int arrivalTime; // arrival time of the process
	private ArrayList<Integer> cpuBurst = new ArrayList<>(); // Array of CPU burst lengths in unit time
	private int currCpuBurst;
	private int priority; // priority level of the process
	private ArrayList<Integer> ioBurst  = new ArrayList<>(); // Array of IO burst lengths in unit time
	private int currIoBurst;
	private int cpuIndex; // Keeps track of CPU burst index;
	private int ioIndex; // Keeps track of IO burst index;
	// the statistics of process execution
	private int startTime, finishTime, turnaroundTime, waitingTime;

	// constructor
	public PCB(String name, int id, int arrivalTime, int priority, ArrayList<Integer> cpuBurst, ArrayList<Integer> ioBurst) {
		super();
		//New variables used to track the current burst times for CPU and IO
		this.currCpuBurst = cpuBurst.get(cpuIndex); 
		this.currIoBurst = ioBurst.get(ioIndex);
		this.name = name;
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.cpuBurst = cpuBurst;
		this.priority = priority;
		this.ioBurst = ioBurst;
		this.startTime = -1;
		this.finishTime = -1;
		this.cpuIndex = 0;
		this.ioIndex = 0;
		
		// Calculates total CPU and IO burst times;
		for(int value : cpuBurst) {
			totalCpuBurstTime += value;
		}
		for(int value : ioBurst) {
			totalIoBurstTime += value;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	//Gets current CPU burst time
	public int getCpuBurst() {
		return currCpuBurst;
	}
	
	//Gets total CPU Burst Time
	public int getTotalCpuBurstTime() {
		return totalCpuBurstTime;
	}
	
	//Gets total Io Burst Time
	public int getTotalIoBurstTime() {
		return totalIoBurstTime;
	}
	
	public void setTotalCpuBurstTime(int value) {
		totalCpuBurstTime = value;
	}
	
	public boolean endOfCpuBurstArray() {
		return cpuIndex == cpuBurst.size();
	}
	
	//Gets total IO Burst Time
	public void setTotalIoBurstTime(int value) {
		totalIoBurstTime = value;
	}
	
	public ArrayList<Integer> getCpuBurstArray(){
		return cpuBurst;
	}
	
	public int getNextCpuBurst() {
		cpuIndex += 1;
		return cpuBurst.get(cpuIndex); 
		
	}

	//Changes current CPU burst time
	public void setCpuBurst(int cpuBurst) {
		currCpuBurst = cpuBurst;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	//Gets current IO burst time
	public int getIoBurst() {
		return currIoBurst; 
	}

	//Changes current IO burst time
	public void setIoBurst(int ioBurst) {
		currIoBurst = ioBurst;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(int finishTime) {
		this.finishTime = finishTime;
		this.turnaroundTime = finishTime - arrivalTime;
	}

	public int getTurnaroundTime() {
		return turnaroundTime;
	}

	public void setTurnaroundTime(int turnaroundTime) {
		this.turnaroundTime = turnaroundTime;
	}

	public int getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}

	public void increaseWaitingTime(int burst) {
		// Increase the waitingTime variable with burst.
		this.waitingTime += burst;
	}

	public String toString() {
		return "Process [name=" + name + ", id=" + id
				+ ", arrivalTime=" + arrivalTime + ", cpuBurst=" + currCpuBurst //Changed to represent current burst
				+ ", priority=" + priority + "]";
	}

}
