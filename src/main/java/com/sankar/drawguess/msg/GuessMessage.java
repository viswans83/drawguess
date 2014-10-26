package com.sankar.drawguess.msg;

import com.sankar.drawguess.api.IPlayer;

public class GuessMessage extends IncommingMessage {
	
	@SuppressWarnings("unused")
	private String who;
	private String guess;
	
	public GuessMessage() {}
	
	public GuessMessage(String guess) {
		this.guess = guess;
	}
	
	public void setWhoGuessed(String who) {
		this.who = who;
	}
	
	public String getGuess() {
		return guess;
	}

	@Override
	public void dispatchTo(IPlayer player) {
		player.guess(this);
	}
	
}
