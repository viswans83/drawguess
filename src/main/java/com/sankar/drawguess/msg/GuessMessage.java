package com.sankar.drawguess.msg;

public class GuessMessage extends Message {
	
	@SuppressWarnings("unused")
	private String who;
	private String guess;
	
	public void setWho(String who) {
		this.who = who;
	}
	
	public String getGuess() {
		return guess;
	}
	
}
