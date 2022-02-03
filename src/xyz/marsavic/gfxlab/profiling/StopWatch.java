package xyz.marsavic.gfxlab.profiling;

import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;

import xyz.marsavic.gfxlab.gui.Profiling;

public class StopWatch {
	private long startTime;
	private ArrayList<ProfilingLogWriter> writers;
	
	
	public StopWatch() {
		this.writers = new ArrayList<>();
		startTime = 0;
	}
	
	public void addDefaultWriter(ProfilingLogWriter writer) {
		this.writers.add(writer);
	}
	
	public void start() {
		startTime = System.nanoTime();
	}
	public long take() {
		long endTime = System.nanoTime();
		long measurement = endTime - this.startTime;
		this.startTime = endTime;
		for (ProfilingLogWriter writer : writers) {
			writer.write(measurement);
		}
		return measurement;
		
	}
		
	public long stopAndWrite(ProfilingLogWriter writer) {
		long nanoSeconds = this.take();
		writer.write(nanoSeconds);
		return nanoSeconds;
	}
}
