package com.sankar.drawguess;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GameInProgressMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.InsufficientPlayersMessage;
import com.sankar.drawguess.msg.Message;
import com.sankar.drawguess.msg.PlayerJoinedMessage;
import com.sankar.drawguess.msg.PlayerQuitMessage;

class Room implements IRoom {
	
	private static Logger log = LogManager.getLogger();
	
	private String name;
	private Timer timer;
	
	private List<IPlayer> players = new ArrayList<>();
	
	private IGame game;
	
	public Room(String name, Timer timer) {
		this.name = name;
		this.timer = timer;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public synchronized void playerJoined(IPlayer player) {
		players.add(player);
		
		sendMessage(new PlayerJoinedMessage(player.getName()));
		
		if (game == null)
			if (players.size() == MIN_PLAYERS_PER_GAME)
				startNewGame();
			else
				player.sendMessage(new InsufficientPlayersMessage());
		
		else
			player.sendMessage(new GameInProgressMessage());
	}
	
	@Override
	public synchronized void playerQuit(IPlayer player) {
		players.remove(player);
		
		sendMessage(new PlayerQuitMessage(player.getName()));
		
		if (game != null)
			game.playerQuit(player);
	}
	
	@Override
	public synchronized void playerGuessed(GuessMessage message, IPlayer player) {
		if (game == null) return;
		
		game.playerGuessed(message, player);
	}
	
	@Override
	public synchronized void playerDrew(DrawingMessage drawing, IPlayer player) {
		if (game == null) return;
		
		game.playerDrew(drawing, player);
	}
	
	@Override
	public synchronized void sendMessage(Message message) {
		for (IPlayer p : players)
			p.sendMessage(message);
	}
	
	@Override
	public synchronized void sendMessageToAllBut(IPlayer player, Message message) {
		for (IPlayer p : players)
			if (!p.equals(player))
				p.sendMessage(message);
	}
	
	private synchronized void startNewGame() {
		game = new Game(players, this, timer);
		game.start();
	}

	@Override
	public synchronized void gameOver() {
		game = null;
		
		if (players.size() >= MIN_PLAYERS_PER_GAME)
			startNewGame();
		else
			sendMessage(new InsufficientPlayersMessage());
	}

	@Override
	public synchronized boolean isPresent(IPlayer player) {
		return players.contains(player);
	}
	
}
