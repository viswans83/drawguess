package com.sankar.drawguess.msg;

import com.sankar.drawguess.Player;

public class WordGuessedMessage extends Message {
	
	@SuppressWarnings("unused")
	private String who;
	
	@SuppressWarnings("unused")
	private boolean wordGuessed = true;
	
	public WordGuessedMessage(Player who) {
		this.who = who.getName();
	}
	
}
