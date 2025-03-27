package lab04;

import java.io.*;
import java.util.*;

public class Driver {
	//static Scanner objects to be used throughout the program
	private static Scanner paramScanner;
	private static Scanner sc;
	private static Scanner simInput;

	// Close all scanners
	public static void closeScanners() {
		paramScanner.close();
		sc.close();
	}

	// Main driver method split out for resuability. Static ensures that there is only ever one instance of this method.
	public static void executeDriver(String filePath) throws FileNotFoundException {
		int quantumTime = 0;
		paramScanner = new Scanner(System.in);

		// ask user for scheduling algorithm
		System.out.println("Choose scheduling algoritm (FCFS, PS, SJF, RR)");
		String alg = paramScanner.nextLine();

		// ask user for quantum time if RR is selected
		if (alg.equals("RR")) {
			System.out.println("Enter quantum time:");
			quantumTime = paramScanner.nextInt(); // look for an integer
		}

		// ask user for running mode
		System.out.println("Choose running mode number (0 = auto, 1 = manual)");
		int runningMode = paramScanner.nextInt(); // look for an integer

		sc = new Scanner(new File(filePath)); // open the file for reading
		String line;
		int id = 0;
		ArrayList<PCB> allProcs = new ArrayList<>(); // list of processes

		// read process data from file line by line
		while (sc.hasNextLine()) { // while there are lines to read
			ArrayList<Integer> bursts = new ArrayList<>();
			bursts.clear(); // resets array list

			line = sc.nextLine(); // read a line from the file
			String[] arr = line.split(" "); // split the line into an array of strings via regular expression

			// parse process data
			String name = arr[0];
			int priority = Integer.parseInt(arr[1].trim()); // parse the priority
			int arrivalTime = Integer.parseInt(arr[2].trim()); // parse the arrival time

			// collect CPU/IO burst times into a list
			for (int i = 3; i < arr.length; i++) {
				bursts.add(Integer.parseInt(arr[i].trim()));
			}

			// create PCB object for each process
			PCB proc = new PCB(name, id++, arrivalTime, priority, bursts);
			allProcs.add(proc);
			System.out.println();// print a blank line for ease of reading
		}

		// Instantiate the scheduler based on user input
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

		// Set the running mode
		if (runningMode == 1) {
			scheduler.setManualMode(true);
		} else {
			scheduler.setManualMode(false);
		}

		scheduler.schedule();
	}

	// Main method
	public static void main(String[] args) throws FileNotFoundException {
		simInput = new Scanner(System.in);
		boolean termSim = false;
		// Ask user for file path and save to a string
		System.out.println("Proivide the file path to desired scenario file:");
		String filePath = simInput.nextLine();
		String response = "";

		// Do while loop to allow user to run multiple simulations while forcing them to
		// proform atleast one.
		do {
			executeDriver(filePath);
			while(!response.equals("Y") && !response.equals("N")){
				System.out.println("Try another algorithm? [Y/N]");
				response = simInput.nextLine();
			}
			if (response.equalsIgnoreCase("N")) {
				termSim = true;
			}
			response = "";
		} while (termSim != true); // while the user does not enter "N"

		// Clean up resources
		closeScanners();
		simInput.close();
	}
}
