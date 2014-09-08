package com.sankar.drawguess.api;

import com.sankar.drawguess.Timer.Action;

public interface ITimer {

	void registerInterest(ITimed timed);

	void unregisterInterest(ITimed timed);

	void schedule(int ticks, Action task);

}