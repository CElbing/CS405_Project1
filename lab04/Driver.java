package lab04;
import java.io.*;
import java.util.*;

public class Driver {

	public static void main(String[] args) throws FileNotFoundException{
		Scanner sc = new Scanner(new File("proc.txt"));
		String alg = sc.nextLine(); //read the selected algorithm,
		String line;
		int id = 0;
		ArrayList<PCB> allProcs = new ArrayList<>(); //list of processes
		ArrayList<Integer> cpuBurstTime = new ArrayList<>();
		ArrayList<Integer> ioBurstTime = new ArrayList<>();
		
		while (sc.hasNextLine()) {
			line = sc.nextLine(); // read a line from the file
			String[] arr = line.split(", ");
			String name = arr[0];
			int arrivalTime = Integer.parseInt(arr[1].trim());
			int priority = Integer.parseInt(arr[2].trim());
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
			PCB proc = new PCB(name, id++, arrivalTime,cpuBurstTime,priority,ioBurstTime);
			allProcs.add(proc);
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
		default: 
			scheduler = new FCFS(allProcs);
			break;
		}
		scheduler.schedule();
		sc.close();
	}

}
