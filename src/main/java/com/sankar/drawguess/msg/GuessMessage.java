package com.sankar.drawguess.msg;

import com.sankar.drawguess.Player;

public class GuessMessage extends Message {
	
	@SuppressWarnings("unused")
	private String who;
	private String guess;
	
	public void setWhoGuessed(Player who) {
		this.who = who.getName();
	}
	
	public String getGuess() {
		return guess;
	}
	
}
