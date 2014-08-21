package com.sankar.drawguess.msg;

public class PlayerJoinedMessage extends Message {
	
	@SuppressWarnings("unused")
	private String playerJoined;
	
	public PlayerJoinedMessage(String name) {
		this.playerJoined = name;
	}
	
}
