package lab04;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

public class PriorityScheduling extends SchedulingAlgorithm {
	public PriorityScheduling(List<PCB> queue) throws FileNotFoundException {
		super("Priority Scheduling", queue);
	}

	public PCB pickNextProcess() {
		Collections.sort(readyQueue, (pcb1, pcb2) -> pcb1.getPriority() - pcb2.getPriority());
		if (readyQueue.size() >= 1) {
			if(prevProcess != readyQueue.get(0) && prevProcess != null){
				logWriter.println();
				System.out.println(prevProcess + " interrupted by " + readyQueue.get(0)
						+ " due to higher priority.");
				logWriter.println(prevProcess + " interrupted by " + readyQueue.get(0)
						+ " due to higher priority. \nSystem time: " + systemTime);
				System.out.println();
			}
			readyQueue.get(0).setState("RUNNING");
			return readyQueue.get(0); // first PCB now has the highest priority
		} else {
			return null;
		}

	}
}
