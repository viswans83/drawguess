package com.sankar.drawguess;

import com.sankar.drawguess.api.IEndPoint;
import com.sankar.drawguess.api.IPlayer;
import com.sankar.drawguess.api.IRoom;
import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.Message;

public class Player implements IPlayer {
	
	private String name;
	private IRoom room;
	
	private IEndPoint ep;
	
	public Player(String name, IEndPoint ep) {
		this.name = name;
		this.ep = ep;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void sendMessage(Message message) {
		ep.sendMessage(message);
	}
	
	@Override
	public void joinRoom(IRoom room) {
		if (this.room != null) throw new IllegalStateException();
			
		this.room = room;
		room.playerJoined(this);
	}
	
	@Override
	public void leaveCurrentRoom() {
		if (room == null) throw new IllegalStateException();
		
		room.playerQuit(this);
		room = null;
	}
	
	@Override
	public void guessed(GuessMessage guess) {
		if (room == null) throw new IllegalStateException();
		
		guess.setWhoGuessed(this);
		room.playerGuessed(guess, this);
	}
	
	@Override
	public void drew(DrawingMessage drawing) {
		if (room == null) throw new IllegalStateException();
		
		room.playerDrew(drawing, this);
	}
	
}
