package com.sankar.drawguess.mocks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sankar.drawguess.api.IGame;
import com.sankar.drawguess.api.IPlayer;
import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.Message;

public class MockGame implements IGame {
	
	private boolean started;

	@Override
	public void start() {
		started = true;
	}
	
	public boolean didStart() {
		return started;
	}
	
	private Map<IPlayer, Integer> pts = new HashMap<>();

	@Override
	public void award(IPlayer player, int points) {
		Integer p = pts.get(player);
		if (p == null)
			pts.put(player, points);
		else
			pts.put(player, p + points);
	}
	
	public boolean didScore(IPlayer player) {
		return pts.get(player) != null;
	}
	
	public int score(IPlayer player) {
		return pts.get(player);
	}
	
	private Map<Class<? extends Message>, Integer> messagesRecieved = new HashMap<>();

	@Override
	public void sendMessage(Message message) {
		Integer cnt = messagesRecieved.get(message.getClass());
		messagesRecieved.put(message.getClass(), cnt == null ? 1 : cnt + 1);
	}

	@Override
	public void sendMessageToAllBut(IPlayer player, Message message) {
		sendMessage(message);
	}
	
	public boolean didRecieveMessageOfType(Class<? extends Message> type) {
		return messagesRecieved.containsKey(type);
	}
	
	public int totalMessagesRecieved() {
		int tot = 0;
		for (Class<? extends Message> mc : messagesRecieved.keySet()) {
			tot += messagesRecieved.get(mc);
		}
		return tot;
	}
	
	public int totalMessagesRecievedOfType(Class<? extends Message> type) {
		Integer cnt = messagesRecieved.get(type);
		return cnt == null ? 0 : cnt;
	}
	
	private boolean roundCompleted;

	@Override
	public void roundComplete() {
		roundCompleted = true;
	}
	
	public boolean didRoundComplete() {
		return roundCompleted;
	}
	
	private Set<IPlayer> joined = new HashSet<>();

	@Override
	public void playerJoined(IPlayer player) {
		joined.add(player);
	}
	
	public boolean didJoin(IPlayer player) {
		return joined.contains(player);
	}
	
	private Map<IPlayer, GuessMessage> guessed = new HashMap<>();

	@Override
	public void playerGuessed(GuessMessage message, IPlayer player) {
		guessed.put(player, message);
	}
	
	public boolean didGuess(IPlayer player) {
		return guessed.get(player) != null;
	}
	
	private Map<IPlayer, DrawingMessage> drew = new HashMap<>();

	@Override
	public void playerDrew(DrawingMessage drawing, IPlayer player) {
		drew.put(player, drawing);
	}
	
	public boolean didDraw(IPlayer player) {
		return drew.get(player) != null;
	}
	
	private Set<IPlayer> quits = new HashSet<>();

	@Override
	public void playerQuit(IPlayer player) {
		quits.add(player);
	}
	
	public boolean didQuit(IPlayer player) {
		return quits.contains(player);
	}

}
