package com.sankar.drawguess;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Timer extends Thread {

	private Set<Timed> interested = Collections.synchronizedSet(new HashSet<Timed>());
	
	public Timer() {
		setName("Game Timer");
		setDaemon(true);
	}
	
	public void registerInterest(Timed timed) {
		interested.add(timed);
	}
	
	public void unregisterInterest(Timed timed) {
		interested.remove(timed);
	}
	
	public void schedule(int ticks, Action task) {
		new Delay(ticks, task).start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
				synchronized(interested) {
					for (Timed t : interested) {
						t.tick();
					}
				}
			} catch(InterruptedException e) {
				break;
			}
		}
	}
	
	public static interface Action {
		void run();
	}
	
	public class Delay implements Timed {
		
		private int ticksRemaining;
		private Action task;
		
		private boolean started;
		private boolean expired;
		
		Delay(int ticks, Action task) {
			this.ticksRemaining = ticks;
			this.task = task;
		}

		@Override
		public synchronized void tick() {
			if (!started || expired) throw new IllegalStateException();
			
			if (ticksRemaining-- == 0) {
				expire();
				runTask();
			}
		}
		
		public void start() {
			if (started) return;
			
			started = true;
			registerInterest(this);
		}
		
		public synchronized void cancel() {
			expire();
		}
		
		private void expire() {
			if (expired) return;
			
			expired = true;
			unregisterInterest(this);
		}
		
		private void runTask() {
			task.run();
		}

	}
	
}
