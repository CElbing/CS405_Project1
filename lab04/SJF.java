package lab04;

import java.util.Collections;
import java.util.List;

public class SJF extends SchedulingAlgorithm {
      public SJF (List<PCB> queue) {
		super("SJF", queue);
	}

      public PCB pickNextProcess() {
   //Sort the ready queue in asc order of CPU burst
   	Collections.sort(readyQueue, (pcb1,pcb2) -> pcb1.getBurst() - pcb2.getBurst());
		return readyQueue.get(0); // First one now has the smallest CPU burst. 
      }
}
