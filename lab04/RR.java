package lab04;

import java.io.FileNotFoundException;
import java.util.*;

public class RR extends SchedulingAlgorithm {
    public RR(List<PCB> queue) throws FileNotFoundException {
        super("RR", queue);
    }

    private static int count = 0;

    public static void setCount(int i) {
        count = i;
    }

    @Override
    public PCB pickNextProcess() {
        if (readyQueue.size() > 1) {
            if (count == quantum) {
                logWriter.println();
                System.out.println();
                System.out.println(readyQueue.get(0) + " interrupted by " + "\n" +  readyQueue.get(1)
                        + " due to end of quantum time.");
                logWriter.println(readyQueue.get(0) + " interrupted by " + "\n" + readyQueue.get(1)
                        + " due to end of quantum time. \nSystem time: " + systemTime);
                PCB proc = readyQueue.get(0);
                readyQueue.remove(proc);
                readyQueue.add(proc);
                count = 0;
            }
            count++;
        } else {
            count = 0;
        }
        return readyQueue.get(0);
    }

}
