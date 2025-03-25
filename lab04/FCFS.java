package lab04;

import java.io.FileNotFoundException;
import java.util.List;

public class FCFS extends SchedulingAlgorithm {
	public FCFS(List<PCB> queue) throws FileNotFoundException {
		super("FCFS", queue);
	}

	public PCB pickNextProcess() {
		if (readyQueue.isEmpty())
			return null;
		return readyQueue.get(0);
	}
}
