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
	
}
