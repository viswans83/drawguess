package com.sankar.drawguess;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.sankar.drawguess.api.ITimed;
import com.sankar.drawguess.api.ITimer;

public class Timer extends Thread implements ITimer {

	private Set<ITimed> interested = Collections.synchronizedSet(new HashSet<ITimed>());
	
	public Timer() {
		setName("Game Timer");
		setDaemon(true);
	}
	
	@Override
	public void registerInterest(ITimed timed) {
		interested.add(timed);
	}
	
	@Override
	public void unregisterInterest(ITimed timed) {
		interested.remove(timed);
	}
	
	@Override
	public void schedule(int ticks, Action task) {
		new Delay(ticks, task).start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
				synchronized(interested) {
					for (ITimed t : interested) {
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
	
	public class Delay implements ITimed {
		
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
