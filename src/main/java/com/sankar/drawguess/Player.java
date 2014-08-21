package com.sankar.drawguess;

import com.sankar.drawguess.msg.Message;

public class Player implements EndPoint {
	
	private String name;
	private EndPoint ep;
	
	public Player(String name, EndPoint ep) {
		this.name = name;
		this.ep = ep;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public void sendMessage(Message message) {
		ep.sendMessage(message);
	}
	
}
