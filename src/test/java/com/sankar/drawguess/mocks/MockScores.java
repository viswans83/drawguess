package com.sankar.drawguess.mocks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sankar.drawguess.api.IEndPoint;
import com.sankar.drawguess.api.IPlayer;
import com.sankar.drawguess.api.IScores;

public class MockScores implements IScores {
	
	private Map<IPlayer, Integer> awards = new HashMap<>();

	@Override
	public void award(IPlayer player, int points) {
		if (!awards.containsKey(player))
			awards.put(player, 0);
		
		awards.put(player, awards.get(player) + points);
	}
	
	public int getPoints(IPlayer player) {
		return awards.containsKey(player) ? awards.get(player) : 0;
	}
	
	private Set<IEndPoint> scoresSentTo = new HashSet<>();

	@Override
	public void transmit(IEndPoint endPoint) {
		scoresSentTo.add(endPoint);
	}
	
	public boolean didTransmitTo(IEndPoint endPoint) {
		return scoresSentTo.contains(endPoint);
	}
	
	public void clearSentTo() {
		scoresSentTo.clear();
	}

}
