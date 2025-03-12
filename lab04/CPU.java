package lab04;
public class CPU {	
	public static void execute(PCB process, int cpuBurst) {
		//simulate the CPU executing a burst cpuBurst unit time of that process.
		process.setCpuBurst(process.getCpuBurst() - cpuBurst);
	}
}
