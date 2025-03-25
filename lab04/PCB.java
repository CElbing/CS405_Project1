package lab04;

import java.util.*;

public class PCB {
	// the representation of each process
	private String name; // process name
	private int id; // process id
	private int arrivalTime; // arrival time of the process
	private ArrayList<Integer> bursts = new ArrayList<>(); // Array of CPU burst lengths in unit time
	private int priority; // priority level of the process
	private int index; // Keeps track of IO burst index;
	// the statistics of process execution
	private String state;
	private int ioWaitingTime;
	private int startTime, finishTime, turnaroundTime, waitingTime;

	// constructor
	public PCB(String name, int id, int arrivalTime, int priority, ArrayList<Integer> bursts) {
		super();
		// New variables used to track the current burst times for CPU and IO
		this.name = name;
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.priority = priority;
		this.index = 0;
		this.startTime = -1;
		this.finishTime = -1;
		this.bursts = bursts;
		this.ioWaitingTime = 0;
		this.state = "";
	}

	public int getBurst() {
		if (index >= 0 && index < bursts.size())
			return bursts.get(index);
		else
			return 0;
	}

	public void setBurst(int curBurst) {
		bursts.set(index, curBurst);
	}

	public int getIndex() {
		return this.index;
	}

	public void setIndex(int i) {
		if (index >= 0 && index < bursts.size() - 1) {
			this.index = i;
		}
	}

	public ArrayList<Integer> getBurstArray() {
		return this.bursts;
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

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
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

	public int getIOWaitingTime() {
		return ioWaitingTime;
	}

	public void setIOWaitingTime(int waitingTime) {
		this.ioWaitingTime = waitingTime;
	}

	public void increaseWaitingTime(int burst) {
		// Increase the waitingTime variable with burst.
		this.waitingTime += burst;
	}

	public void increaseIOWaitingTime(int burst) {
		this.ioWaitingTime += burst;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String toString() {
		return "Process [name=" + name + ", id=" + id + ", state= " + state
				+ ", arrivalTime=" + arrivalTime + ", burst=" + bursts.get(index) // Changed to represent current burst
				+ ", priority=" + priority + "]";
	}

}
