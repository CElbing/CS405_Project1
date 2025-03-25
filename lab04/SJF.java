package lab04;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

public class SJF extends SchedulingAlgorithm {
      public SJF(List<PCB> queue) throws FileNotFoundException {
            super("SJF", queue);
      }

      public PCB pickNextProcess() {
            // Sort the ready queue in asc order of CPU burst
            Collections.sort(readyQueue, (pcb1, pcb2) -> pcb1.getBurst() - pcb2.getBurst());
            if (readyQueue.size() >= 1) {
                  if(prevProcess != readyQueue.get(0) && prevProcess != null){
                        logWriter.println();
				System.out.println(prevProcess + " interrupted by " + readyQueue.get(0)
						+ " due to smaller burst size.");
				logWriter.println(prevProcess + " interrupted by " + readyQueue.get(0)
						+ " due to smaller burst size. \nSystem time: " + systemTime);
				System.out.println();
			}
			readyQueue.get(0).setState("RUNNING");
                  return readyQueue.get(0); // first PCB now has the lowest burst.
            } else {
                  return null;
            }
      }
}
