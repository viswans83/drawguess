package com.sankar.drawguess;

import com.sankar.drawguess.msg.AwardMessage;
import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.Message;
import com.sankar.drawguess.msg.PlayersMessage;
import com.sankar.drawguess.msg.ScoresMessage;

public class Player implements EndPoint {
	
	private String name;
	private int score;
	private Room room;
	
	private EndPoint ep;
	
	public Player(String name, EndPoint ep) {
		this.name = name;
		this.ep = ep;
	}
	
	public String getName() {
		return name;
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
		room.playerGuessed(guess, this);
	}
	
	public void drew(DrawingMessage drawing) {
		room.playerDrew(drawing);
	}
	
	public void populate(ScoresMessage msg) {
		msg.add(name, score);
	}
	
	public void populate(PlayersMessage msg, boolean currentlyDrawing) {
		msg.add(name, score, currentlyDrawing);
	}
	
}
