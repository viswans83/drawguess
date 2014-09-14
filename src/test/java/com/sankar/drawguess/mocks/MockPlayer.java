package com.sankar.drawguess.mocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sankar.drawguess.api.IPlayer;
import com.sankar.drawguess.api.IRoom;
import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.Message;

public class MockPlayer implements IPlayer {
	
	private String name;
	
	public MockPlayer(String name) {
		this.name = name;
	}
	
	private Map<Class<? extends Message>, List<Message>> messages = new HashMap<>();

	@Override
	public void sendMessage(Message message) {
		if (!messages.containsKey(message.getClass()))
			messages.put(message.getClass(), new ArrayList<Message>());
		
		messages.get(message.getClass()).add(message);
	}
	
	public Message lastMessageRecievedOfType(Class<? extends Message> type) {
		int size = messages.get(type).size();
		return messages.get(type).get(size - 1);
	}
	
	public boolean didRecieveMessageOfType(Class<? extends Message> type) {
		return messages.get(type).size() > 0;
	}
	
	public int countRecievedMessageOfType(Class<? extends Message> type) {
		return messages.get(type).size();
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	private IRoom room;

	@Override
	public void joinRoom(IRoom room) {
		this.room = room;
	}

	@Override
	public void leaveCurrentRoom() {
		this.room = null;
	}

	public IRoom getRoom() {
		return room;
	}
	
	private GuessMessage lastGuessed;

	@Override
	public void guessed(GuessMessage guess) {
		this.lastGuessed = guess;
	}
	
	public GuessMessage getLastGuessed() {
		return lastGuessed;
	}
	
	private DrawingMessage lastDrawing;

	@Override
	public void drew(DrawingMessage drawing) {
		this.lastDrawing = drawing;
	}
	
	public DrawingMessage getLastDrawing() {
		return lastDrawing;
	}

}
