package lab04;

import java.io.*;
import java.util.*;

public class Driver {
	private static Scanner paramScanner;
	private static Scanner sc;
	private static Scanner simInput;

	public static void closeScanners(){
		paramScanner.close();
		sc.close();
	}

	public static void executeDriver(String filePath) throws FileNotFoundException{
		int quantumTime = 0;
		paramScanner = new Scanner(System.in);
		System.out.println("Choose scheduling algoritm (FCFS, PS, SJF, RR)");
		String alg = paramScanner.nextLine();

		if (alg.equals("RR")) {
			System.out.println("Enter quantum time:");
			quantumTime = paramScanner.nextInt();
		}

		System.out.println("Choose running mode number (0 = auto, 1 = manual)");
		int runningMode = paramScanner.nextInt();

		sc = new Scanner(new File(filePath));
		String line;
		int id = 0;
		ArrayList<PCB> allProcs = new ArrayList<>(); // list of processes

		while (sc.hasNextLine()) {
			ArrayList<Integer> bursts = new ArrayList<>();
			bursts.clear(); // resets array list

			line = sc.nextLine(); // read a line from the file
			String[] arr = line.split(" ");
			String name = arr[0];
			int priority = Integer.parseInt(arr[1].trim());
			int arrivalTime = Integer.parseInt(arr[2].trim());

			// Current issue is that all the CPU burst times exist in one
			// array rather than one array per process.
			for (int i = 3; i < arr.length; i++) {
				bursts.add(Integer.parseInt(arr[i].trim()));
			}
			// int[] cpuBurstTime = Integer.parseInt(arr[2].trim());
			// int[] ioBurst = 0;//Change, should not be 0
			PCB proc = new PCB(name, id++, arrivalTime, priority, bursts);
			allProcs.add(proc);
			System.out.println();
		}

		// ready to simulate the scheduling of those processes.
		SchedulingAlgorithm scheduler = null;
		switch (alg) {
			case "RR":
				scheduler = new RR(allProcs);
				scheduler.setQuantum(quantumTime);
				break;
			case "FCFS":
				scheduler = new FCFS(allProcs);
				break;
			case "SJF":
				scheduler = new SJF(allProcs);
				break;
			case "PS":
				scheduler = new PriorityScheduling(allProcs);
				break;
			default:
				scheduler = new FCFS(allProcs);
				break;
		}
		if (runningMode == 1) {
			scheduler.setManualMode(true);
		} else {
			scheduler.setManualMode(false);
		}

		scheduler.schedule();
	}

	public static void main(String[] args) throws FileNotFoundException {
		simInput = new Scanner(System.in);
		boolean termSim = false;
		System.out.println("Proivide the file path to desired scenario file:");
		String filePath = simInput.nextLine();
		do{
			executeDriver(filePath);
			System.out.println("Try another algorithm? [Y/N]");
			String response = simInput.nextLine();
			if(response.equalsIgnoreCase("N")){
				termSim = true;
			}
		}while(termSim != true);
		closeScanners();
		simInput.close();
	}
}
