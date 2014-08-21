package com.sankar.drawguess.msg;

public class PlayerQuitMessage extends Message {
	
	@SuppressWarnings("unused")
	private String playerQuit;
	
	public PlayerQuitMessage(String name) {
		this.playerQuit = name;
	}

}
