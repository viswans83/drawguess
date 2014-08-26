package com.sankar.drawguess.msg;

import java.util.ArrayList;
import java.util.List;

public class ScoresMessage extends Message {
	
	private List<PlayerLite> scores = new ArrayList<>();
	
	public void add(String name, int score) {
		PlayerLite p = new PlayerLite();
		
		p.name = name;
		p.score = score;
		
		scores.add(p);
	}
	
	private static class PlayerLite {
		@SuppressWarnings("unused")
		String name;
		
		@SuppressWarnings("unused")
		int score;
	}
	
}