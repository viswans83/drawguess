package com.sankar.drawguess.msg;

import com.sankar.drawguess.IPlayer;

public class GuessMessage extends Message {
	
	@SuppressWarnings("unused")
	private String who;
	private String guess;
	
	public void setWhoGuessed(IPlayer who) {
		this.who = who.getName();
	}
	
	public String getGuess() {
		return guess;
	}
	
}
