package lab04;

import java.util.Collections;
import java.util.List;

public class PriorityScheduling extends SchedulingAlgorithm {
      public PriorityScheduling (List<PCB> queue) {
		super("Priority Scheduling", queue);
	}


      public PCB pickNextProcess() {
		// TODO Auto-generated method stub
    	   	Collections.sort(readyQueue, (pcb1,pcb2) -> pcb1.getPriority() - pcb2.getPriority());

    	  return readyQueue.get(0); // first PCB now has the highest priority
    	  
      }
}
