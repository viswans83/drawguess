package com.sankar.drawguess;

import com.sankar.drawguess.msg.AwardMessage;
import com.sankar.drawguess.msg.Message;

public class Player implements EndPoint {
	
	private String name;
	private int score;
	
	private EndPoint ep;
	
	public Player(String name, EndPoint ep) {
		this.name = name;
		this.ep = ep;
	}
	
	public String getName() {
		return name;
	}
	
	public int getScore() {
		return score;
	}
	
	public void award(int points) {
		score = score + points;
		sendMessage(new AwardMessage(points));
	}
	
	public void resetScore() {
		score = 0;
	}
	
	@Override
	public void sendMessage(Message message) {
		ep.sendMessage(message);
	}	
	
}
