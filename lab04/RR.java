package lab04;

import java.io.FileNotFoundException;
import java.util.*;

public class RR extends SchedulingAlgorithm {
    // constructor passing though super
    public RR(List<PCB> queue) throws FileNotFoundException {
        super("RR", queue);
    }

    private static int count = 0; // counter var to track how many units the current process has used. Static to
                                  // ensure only one instance of this variable.

    // setter method to set the counter var
    public static void setCount(int i) {
        count = i;
    }

    @Override
    public PCB pickNextProcess() {
        if (readyQueue.size() > 1) { // checking to see if there is more than one process in the queue
            if (count == quantum) { // checking to see if current process has used up its quantum time
                logWriter.println();

                System.out.println(); // spacing for output

                // logging end of quantum time interuption
                System.out.println(readyQueue.get(0) + " interrupted by " + "\n" + readyQueue.get(1)
                        + " due to end of quantum time.");
                logWriter.println(readyQueue.get(0) + " interrupted by " + "\n" + readyQueue.get(1)
                        + " due to end of quantum time. \nSystem time: " + systemTime);
                // rotate the queue
                PCB proc = readyQueue.get(0);
                readyQueue.remove(proc);
                readyQueue.add(proc);
                count = 0; // reset the counter
            }
            count++; // increment
        } else {
            count = 0; // reset the counter if there is only one process in the queue
        }
        return readyQueue.get(0); // return the process at the front of the queue
    }

}
