package lab04;

import java.util.Collections;
import java.util.List;

public class PriorityScheduling extends SchedulingAlgorithm {
	public PriorityScheduling(List<PCB> queue) {
		super("Priority Scheduling", queue);
	}

	public PCB pickNextProcess() {
		Collections.sort(readyQueue, (pcb1, pcb2) -> pcb1.getPriority() - pcb2.getPriority());
		if (readyQueue.size() >= 1) {
			return readyQueue.get(0); // first PCB now has the highest priority
		}
		else{
			return null;
		}

	}
}
