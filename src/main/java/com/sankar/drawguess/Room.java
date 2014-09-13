package com.sankar.drawguess;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sankar.drawguess.api.IGame;
import com.sankar.drawguess.api.IPlayer;
import com.sankar.drawguess.api.IPlayerSelector;
import com.sankar.drawguess.api.IRoom;
import com.sankar.drawguess.api.IRound;
import com.sankar.drawguess.api.IScores;
import com.sankar.drawguess.api.ITimer;
import com.sankar.drawguess.api.IWordProvider;
import com.sankar.drawguess.api.factory.IRoundFactory;
import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GameInProgressMessage;
import com.sankar.drawguess.msg.GameOverMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.InsufficientPlayersMessage;
import com.sankar.drawguess.msg.Message;
import com.sankar.drawguess.msg.PlayerJoinedMessage;
import com.sankar.drawguess.msg.PlayerQuitMessage;

class Room implements IRoom {
	
	private static Logger log = LogManager.getLogger();
	
	private String name;
	private ITimer timer;
	
	private Set<IPlayer> players = new HashSet<>();
	
	private IGame game;
	
	public Room(String name, ITimer timer) {
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
		
		log.info("Player [{}] joined room [{}]", player.getName(), getName());
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
		
		log.info("Player [{}] quit room [{}]", player.getName(), getName());
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
		log.info("Starting a new game in room [{}]", getName());
		
		Set<IPlayer> playersNow = new HashSet<IPlayer>(players);
		
		IScores scores = new Scores(playersNow);
		IWordProvider wordProvider = new DefaultWordProvider();
		IPlayerSelector playerSelector = new DefaultPlayerSelector(this, playersNow);
		IRoundFactory roundFactory = new IRoundFactory() {
			@Override
			public IRound create(String word, IPlayer pictorist, IGame game) {
				return new Round(word, pictorist, game, timer);
			}
		};
		
		
		game = new Game(players, scores, wordProvider, playerSelector, roundFactory, this);
		game.start();
	}

	@Override
	public synchronized void gameOver() {
		game = null;
		
		log.info("A game completed in room [{}]", getName());
		sendMessage(new GameOverMessage());
		
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
