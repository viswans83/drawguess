package com.sankar.drawguess.msg;

import com.sankar.drawguess.api.IPlayer;

public class StartGuessingMessage extends Message {
	@SuppressWarnings("unused")
	private boolean startGuessing = true;
	
	@SuppressWarnings("unused")
	private String who;
	
	public StartGuessingMessage(IPlayer currentlyDrawing) {
		this.who = currentlyDrawing.getName();
	}
	
}
