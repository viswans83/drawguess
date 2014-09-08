package com.sankar.drawguess;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sankar.drawguess.api.IEndPoint;
import com.sankar.drawguess.api.IPlayer;
import com.sankar.drawguess.api.IScores;
import com.sankar.drawguess.msg.ScoresMessage;

public class Scores implements IScores {
	
	private Map<IPlayer, Integer> scores = new LinkedHashMap<>();
	
	public Scores(Collection<IPlayer> players) {
		for (IPlayer p : players) {
			scores.put(p, 0);
		}
	}
	
	@Override
	public void award(IPlayer player, int points) {
		scores.put(player, scores.get(player) + points);
	}
	
	@Override
	public void transmit(IEndPoint endPoint) {
		endPoint.sendMessage(buildMessage());
	}
	
	private ScoresMessage buildMessage() {
		ScoresMessage scoresMsg = new ScoresMessage();
		for (IPlayer p : scores.keySet()) {
			scoresMsg.add(p.getName(), scores.get(p));
		}
		return scoresMsg;
	}

}
