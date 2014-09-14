package com.sankar.drawguess.mocks;

import com.sankar.drawguess.api.IGame;
import com.sankar.drawguess.api.IPlayer;
import com.sankar.drawguess.api.IRound;
import com.sankar.drawguess.api.IRoundFactory;

public class MockRoundFactory implements IRoundFactory {
	
	private int roundsCreated;
	
	private IRound round;
	
	public MockRoundFactory(IRound round) {
		this.round = round;
	}

	@Override
	public IRound create(String word, IPlayer pictorist, IGame game) {
		roundsCreated++;
		return round;
	}
	
	public int countOfRoundsCreated() {
		return roundsCreated;
	}

}
