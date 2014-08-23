package com.sankar.drawguess.msg;

import java.util.ArrayList;
import java.util.List;

public class PlayersMessage extends Message {
	
	private List<PlayerDTO> players = new ArrayList<>();
	
	public void add(String name, int score, boolean isDrawing) {
		PlayerDTO p = new PlayerDTO();
		
		p.name = name;
		p.score = score;
		p.isDrawing = isDrawing;
		
		players.add(p);
	}
	
}

class PlayerDTO {
	String name;
	int score;
	boolean isDrawing;
}
