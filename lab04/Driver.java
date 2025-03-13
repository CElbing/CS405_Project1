package lab04;
import java.io.*;
import java.util.*;

public class Driver {

	public static void main(String[] args) throws FileNotFoundException{
		//Scanner sc = new Scanner(new File("src/proc.txt"));		
		Scanner paramScanner = new Scanner(System.in);
		System.out.println("Enter the file path of desired scenario file followed by scheduling parameters");
		System.out.println("Please enter null for unused parameters");
		System.out.println("Format: src/example.txt algorithm quantumTime runningMode");
		String[] params = paramScanner.nextLine().split(", ");
		String filePath = params[0];
		String alg = params[1]; //read the selected algorithm,
		String quantumTime = params[2];
		String runningMode = params[3];
		Scanner sc = new Scanner(new File(filePath));	

		
		String line;
		int id = 0;
		ArrayList<PCB> allProcs = new ArrayList<>(); //list of processes
		ArrayList<Integer> cpuBurstTime = new ArrayList<>();
		ArrayList<Integer> ioBurstTime = new ArrayList<>();
		
		while (sc.hasNextLine()) {
			cpuBurstTime.clear(); //resets array list
			ioBurstTime.clear(); //resets array list
			
			line = sc.nextLine(); // read a line from the file
			String[] arr = line.split(",");
			String name = arr[0];
			int arrivalTime = Integer.parseInt(arr[1].trim());
			int priority = Integer.parseInt(arr[2].trim());
			
			//Current issue is that all the CPU burst times exist in one 
			//array rather than one array per process.
			for(int i = 3; i < arr.length; i++){
				if(i % 2 != 0){
					cpuBurstTime.add(Integer.parseInt(arr[i].trim()));
				}
				else{
					ioBurstTime.add(Integer.parseInt(arr[i].trim()));
				}
			}
			//int[] cpuBurstTime = Integer.parseInt(arr[2].trim());
			//int[] ioBurst = 0;//Change, should not be 0
			PCB proc = new PCB(name, id++, arrivalTime, priority, cpuBurstTime, ioBurstTime);
			allProcs.add(proc);
			System.out.println();
		}
		
		//ready to simulate the scheduling of those processes.
		SchedulingAlgorithm scheduler = null;
		switch(alg) {
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
		scheduler.schedule();
		sc.close();
	}

}
