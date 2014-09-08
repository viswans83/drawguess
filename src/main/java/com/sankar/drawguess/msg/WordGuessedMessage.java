package com.sankar.drawguess.msg;

import com.sankar.drawguess.IPlayer;

public class WordGuessedMessage extends Message {
	
	@SuppressWarnings("unused")
	private String who;
	
	@SuppressWarnings("unused")
	private boolean wordGuessed = true;
	
	public WordGuessedMessage(IPlayer who) {
		this.who = who.getName();
	}
	
}
