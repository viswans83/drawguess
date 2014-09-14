package com.sankar.drawguess.mocks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sankar.drawguess.api.IPlayer;
import com.sankar.drawguess.api.IRoom;
import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.Message;

public class MockRoom implements IRoom {
	
	private String name;
	
	public MockRoom(String name) {
		this.name = name;
	}
	
	private Map<Class<? extends Message>, Integer> messagesRecieved = new HashMap<>();

	@Override
	public void sendMessage(Message message) {
		Integer cnt = messagesRecieved.get(message.getClass());
		messagesRecieved.put(message.getClass(), cnt == null ? 1 : cnt + 1);
	}
	
	public boolean didRecieveMessageOfType(Class<? extends Message> type) {
		return messagesRecieved.containsKey(type);
	}
	
	public void clearMessagesRecieved() {
		messagesRecieved.clear();
	}

	@Override
	public String getName() {
		return name;
	}
	
	private Set<IPlayer> players = new HashSet<>();

	@Override
	public void playerJoined(IPlayer player) {
		// Do Nothing
	}

	@Override
	public void playerQuit(IPlayer player) {
		// Do Nothing
	}
	
	public void addPlayer(IPlayer player) {
		players.add(player);
	}
	
	public void removePlayer(IPlayer player) {
		players.remove(player);
	}

	@Override
	public void playerGuessed(GuessMessage message, IPlayer player) {
		// Do Nothing
	}

	@Override
	public void playerDrew(DrawingMessage drawing, IPlayer player) {
		// Do Nothing
	}

	@Override
	public void sendMessageToAllBut(IPlayer player, Message message) {
		sendMessage(message);
	}
	
	private boolean gameOver;

	@Override
	public void gameOver() {
		gameOver = true;
	}
	
	public boolean didGameEnd() {
		return gameOver;
	}

	@Override
	public boolean isPresent(IPlayer player) {
		return players.contains(player);
	}

}
