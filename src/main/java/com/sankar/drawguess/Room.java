package com.sankar.drawguess;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sankar.drawguess.msg.AwardMessage;
import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.EmptyRoomMessage;
import com.sankar.drawguess.msg.GameInProgressMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.Message;
import com.sankar.drawguess.msg.NewGameMessage;
import com.sankar.drawguess.msg.NewWordMessage;
import com.sankar.drawguess.msg.PlayerJoinedMessage;
import com.sankar.drawguess.msg.PlayerQuitMessage;
import com.sankar.drawguess.msg.StartGuessingMessage;

class Room implements EndPoint {
	
	private static Logger log = LogManager.getLogger();
	
	private String name;
	
	private Player currentlyDrawingPlayer;
	private String currentWord;
	
	private List<DrawingMessage> drawings = new ArrayList<>();
	
	private List<Player> players = new ArrayList<>();
	private int nextPlayerToDrawIndex;
	
	private WordProvider wordProvider = new WordProvider();
	
	public Room(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public synchronized Player getCurrentlyDrawingPlayer() {
		return currentlyDrawingPlayer;
	}
	
	public synchronized void playerJoined(Player player) {
		players.add(player);
		
		switch (playerCount()) {
		case 1: 
			player.sendMessage(new EmptyRoomMessage());
			log.info("Player [{}] joined empty room [{}]", player.getName(), getName());
			break;
		
		case 2:
			sendMessageToAllBut(player, new PlayerJoinedMessage(player.getName()));
			log.info("Player [{}] joined room [{}] containing one waiting player", player.getName(), getName());
			startNewGame();
			break;
		
		default:
			sendMessageToAllBut(player, new PlayerJoinedMessage(player.getName()));
			player.sendMessage(new GameInProgressMessage());
			log.info("Player [{}] joined room [{}] having an in-progress game", player.getName(), getName());
			for (DrawingMessage drawing : drawings) {
				player.sendMessage(drawing);
			}
		}
	}
	
	public synchronized void playerQuit(Player player) {
		players.remove(player);
		
		switch (playerCount()) {
		case 0:
			log.info("Player [{}] quit room [{}] leaving it empty", player.getName(), getName());
			break;
		
		case 1:
			sendMessage(new PlayerQuitMessage(player.getName()));
			sendMessage(new EmptyRoomMessage());
			log.info("Player [{}] quit room [{}] leaving one waiting player", player.getName(), getName());
			break;
		
		default:
			sendMessage(new PlayerQuitMessage(player.getName()));
			log.info("Player [{}] quit room [{}]", player.getName(), getName());
			if (player.equals(currentlyDrawingPlayer)) {
				startNewGame();
			}
		}
	}
	
	public synchronized void playerGuessed(GuessMessage message, Player player) {
		if (message.getGuess().equalsIgnoreCase(currentWord)) {
			log.info("Player [{}] guessed the word", player.getName());
			player.sendMessage(new AwardMessage(10));
			currentlyDrawingPlayer.sendMessage(new AwardMessage(10));
			startNewGame();
		}
	}
	
	public synchronized void playerDrew(DrawingMessage drawing) {
		drawings.add(drawing);
		sendMessageToAllBut(currentlyDrawingPlayer, drawing);
	}
	
	public synchronized int playerCount() {
		return players.size();
	}
	
	@Override
	public synchronized void sendMessage(Message message) {
		for (Player p : players)
			p.sendMessage(message);
	}
	
	public synchronized void sendMessageToAllBut(Player player, Message message) {
		for (Player p : players)
			if (!p.equals(player))
				p.sendMessage(message);
	}
	
	private void startNewGame() {
		log.info("Starting a new game in room [{}]", getName());
		drawings.clear();
		
		selectNewWord();
		selectNextPlayerToDraw();
		
		sendMessage(new NewGameMessage());
		currentlyDrawingPlayer.sendMessage(new NewWordMessage(currentWord));
		sendMessageToAllBut(currentlyDrawingPlayer, new StartGuessingMessage());
	}
	
	private void selectNewWord() {
		currentWord = wordProvider.nextWord();
	}
	
	private void selectNextPlayerToDraw() {
		if (nextPlayerToDrawIndex >= players.size()) {
			nextPlayerToDrawIndex = 0;
		}
		
		currentlyDrawingPlayer = players.get(nextPlayerToDrawIndex++);
		log.info("[{}] will draw next in room [{}]", currentlyDrawingPlayer.getName(), getName());
	}
	
}