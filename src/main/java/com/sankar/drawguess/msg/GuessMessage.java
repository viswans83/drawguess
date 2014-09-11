package com.sankar.drawguess.msg;

import com.sankar.drawguess.api.IPlayer;

public class GuessMessage extends Message {
	
	@SuppressWarnings("unused")
	private String who;
	private String guess;
	
	public GuessMessage() {}
	
	public GuessMessage(String guess) {
		this.guess = guess;
	}
	
	public void setWhoGuessed(IPlayer who) {
		this.who = who.getName();
	}
	
	public String getGuess() {
		return guess;
	}
	
}
