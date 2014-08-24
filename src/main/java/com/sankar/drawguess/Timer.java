package com.sankar.drawguess;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Timer extends Thread {
	
	private Set<Room> rooms = Collections.synchronizedSet(new HashSet<Room>());
	
	public Timer() {
		setName("Game Timer");
		setDaemon(true);
	}
	
	public void registerInterest(Room room) {
		rooms.add(room);
	}
	
	public void unregisterInterest(Room room) {
		rooms.remove(room);
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
				synchronized(rooms) {
					for (Room r : rooms) {
						r.tick();
					}
				}
			} catch(InterruptedException e) {
				break;
			}
		}
	}
	
}
