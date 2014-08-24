package com.sankar.drawguess.msg;

import com.sankar.drawguess.Player;

public class StartGuessingMessage extends Message {
	@SuppressWarnings("unused")
	private boolean startGuessing = true;
	
	@SuppressWarnings("unused")
	private String who;
	
	public StartGuessingMessage(Player currentlyDrawing) {
		this.who = currentlyDrawing.getName();
	}
	
}
