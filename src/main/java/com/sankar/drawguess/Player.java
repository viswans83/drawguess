package com.sankar.drawguess;

import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.Message;

public class Player implements EndPoint {
	
	private String name;
	private Room room;
	
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
	
	public void joinRoom(Room room) {
		if (this.room != null) throw new IllegalStateException();
			
		this.room = room;
		room.playerJoined(this);
	}
	
	public void leaveCurrentRoom() {
		if (room == null) throw new IllegalStateException();
		
		room.playerQuit(this);
		room = null;
	}
	
	public void guessed(GuessMessage guess) {
		if (room == null) throw new IllegalStateException();
		
		room.playerGuessed(guess, this);
	}
	
	public void drew(DrawingMessage drawing) {
		if (room == null) throw new IllegalStateException();
		
		room.playerDrew(drawing);
	}
	
}
