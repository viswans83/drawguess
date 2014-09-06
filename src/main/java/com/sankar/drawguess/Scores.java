package com.sankar.drawguess;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sankar.drawguess.msg.ScoresMessage;

public class Scores {
	
	private Map<Player, Integer> scores = new LinkedHashMap<>();
	
	public Scores(Collection<Player> players) {
		for (Player p : players) {
			scores.put(p, 0);
		}
	}
	
	public void award(Player player, int points) {
		scores.put(player, scores.get(player) + points);
	}
	
	public void transmit(EndPoint endPoint) {
		endPoint.sendMessage(buildMessage());
	}
	
	private ScoresMessage buildMessage() {
		ScoresMessage scoresMsg = new ScoresMessage();
		for (Player p : scores.keySet()) {
			p.populate(scoresMsg);
		}
		return scoresMsg;
	}

}
