package com.sankar.drawguess.msg;

import java.util.ArrayList;
import java.util.List;

public class PlayersMessage extends Message {
	
	private List<PlayerLite> players = new ArrayList<>();
	
	public void add(String name, int score, boolean isDrawing) {
		PlayerLite p = new PlayerLite();
		
		p.name = name;
		p.score = score;
		p.isDrawing = isDrawing;
		
		players.add(p);
	}
	
	private static class PlayerLite {
		@SuppressWarnings("unused")
		String name;
		
		@SuppressWarnings("unused")
		int score;
		
		@SuppressWarnings("unused")
		boolean isDrawing;
	}
	
}