package com.sankar.drawguess.api;

public interface ITimer {

	void registerInterest(ITimed timed);

	void unregisterInterest(ITimed timed);

	void schedule(int ticks, Action task);
	
	public interface Action {
		void run();
	}

}