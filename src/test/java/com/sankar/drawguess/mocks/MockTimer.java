package com.sankar.drawguess.mocks;

import java.util.Collection;
import java.util.HashSet;

import com.sankar.drawguess.api.ITimed;
import com.sankar.drawguess.api.ITimer;

public class MockTimer implements ITimer {
	
	private Collection<ITimed> coll = new HashSet<>();

	@Override
	public void registerInterest(ITimed timed) {
		coll.add(timed);
	}

	@Override
	public void unregisterInterest(ITimed timed) {
		coll.remove(timed);
	}

	@Override
	public void schedule(int ticks, Action task) {
		throw new UnsupportedOperationException();
	}
	
	public boolean isRegistered(ITimed timed) {
		return coll.contains(timed);
	}
	
	public void tick() {
		for (ITimed timed : coll)
			timed.tick();
	}
	
	public void tick(int times) {
		while (times-- > 0) {
			tick();
		}
	}

}
