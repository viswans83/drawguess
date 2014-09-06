package com.sankar.drawguess;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.EmptyRoomMessage;
import com.sankar.drawguess.msg.GameInProgressMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.Message;
import com.sankar.drawguess.msg.NewRoundMessage;
import com.sankar.drawguess.msg.NewWordMessage;
import com.sankar.drawguess.msg.PlayerJoinedMessage;
import com.sankar.drawguess.msg.PlayerQuitMessage;
import com.sankar.drawguess.msg.PlayersMessage;
import com.sankar.drawguess.msg.RoundCompleteMessage;
import com.sankar.drawguess.msg.ScoresMessage;
import com.sankar.drawguess.msg.StartGuessingMessage;
import com.sankar.drawguess.msg.TickMessage;
import com.sankar.drawguess.msg.WordGuessedMessage;

class Room implements Timed {
	
	private static Logger log = LogManager.getLogger();
	
	private String name;
	
	private volatile Player currentlyDrawingPlayer;
	private String currentWord;
	
	private List<DrawingMessage> drawingsInRound = new ArrayList<>();
	
	private List<Player> players = new ArrayList<>();
	private int nextPlayerToDrawIndex;
	
	private int roundNumber;
	private AtomicInteger ticks = new AtomicInteger();
	
	private volatile RoomState roomState = RoomState.WAIT_FOR_PLAYERS;
	
	private WordProvider wordProvider;
	
	public Room(String name, WordProvider wordProvider) {
		this.name = name;
		this.wordProvider = wordProvider;
	}
	
	public String getName() {
		return name;
	}
	
	public synchronized boolean isPlayerStillPresent(Player player) {
		return players.contains(player);
	}
	
	public boolean isRoundInProgress() {
		return roomState == RoomState.ROUND_IN_PROGRESS;
	}
	
	public Player getCurrentlyDrawingPlayer() {
		return currentlyDrawingPlayer;
	}
	
	public synchronized void playerJoined(Player player) {
		boolean startNewGame = false;
		
		players.add(player);
		
		switch (playerCount()) {
		case 1: 
			player.sendMessage(new EmptyRoomMessage());
			log.info("Player [{}] joined empty room [{}]", player.getName(), getName());
			break;
		
		case 2:
			sendMessageToAllBut(player, new PlayerJoinedMessage(player.getName()));
			log.info("Player [{}] joined room [{}] containing one waiting player", player.getName(), getName());
			startNewGame = true;
			break;
		
		default:
			sendMessageToAllBut(player, new PlayerJoinedMessage(player.getName()));
			if (roomState == RoomState.ROUND_IN_PROGRESS) {
				player.sendMessage(new GameInProgressMessage());
				log.info("Player [{}] joined room [{}] having an in-progress game", player.getName(), getName());
			}
			else {
				log.info("Player [{}] joined room [{}]", player.getName(), getName());
			}
			sendDrawings(player);
		}
		
		player.sendMessage(createPlayersMessage());
		
		if (startNewGame) { 
			startNewGame();
		}
	}

	private void sendDrawings(Player player) {
		for (Message drawing : drawingsInRound) {
			player.sendMessage(drawing);
		}
	}

	private PlayersMessage createPlayersMessage() {
		PlayersMessage playersMsg = new PlayersMessage();
		for (Player p : players) {
			playersMsg.add(p.getName(), p.getScore(), p == currentlyDrawingPlayer);
		}
		return playersMsg;
	}
	
	public synchronized void playerQuit(Player player) {
		players.remove(player);
		
		switch (playerCount()) {
		case 0:
			log.info("Player [{}] quit room [{}] leaving it empty", player.getName(), getName());
			break;
		
		case 1:
			roomState = RoomState.WAIT_FOR_PLAYERS;
			sendMessage(new PlayerQuitMessage(player.getName()));
			sendMessage(new EmptyRoomMessage());
			log.info("Player [{}] quit room [{}] leaving one waiting player", player.getName(), getName());
			break;
		
		default:
			sendMessage(new PlayerQuitMessage(player.getName()));
			log.info("Player [{}] quit room [{}]", player.getName(), getName());
			if (isCurrentlyDrawing(player)) {
				startNewRound();
			}
		}
	}
	
	public synchronized void playerGuessed(GuessMessage message, Player player) {
		if (message.getGuess().equalsIgnoreCase(currentWord)) {
			log.info("Player [{}] guessed the word", player.getName());
			message.setWhoGuessed(player);
			sendMessageToAllBut(player, new WordGuessedMessage(player));
			player.award(10);
			currentlyDrawingPlayer.award(10);
		}
		else sendMessageToAllBut(player, message);
	}
	
	public synchronized void playerDrew(DrawingMessage drawing) {
		drawingsInRound.add(drawing);
		sendMessageToAllBut(currentlyDrawingPlayer, drawing);
	}
	
	public synchronized int playerCount() {
		return players.size();
	}
	
	public synchronized void sendMessage(Message message) {
		for (Player p : players)
			p.sendMessage(message);
	}
	
	public synchronized void sendMessageToAllBut(Player player, Message message) {
		for (Player p : players)
			if (!p.equals(player))
				p.sendMessage(message);
	}
	
	private synchronized void startNewGame() {
		roundNumber = 0;
		
		for (Player p : players) {
			p.resetScore();
		}
		
		startNewRound();
	}
	
	private synchronized void startNewRound() {
		log.info("Starting a new game in room [{}]", getName());
		
		roundNumber = roundNumber + 1;
		
		roomState = RoomState.ROUND_IN_PROGRESS;
		ticks.set(0);
		drawingsInRound.clear();
		
		selectNewWord();
		selectNextPlayerToDraw();
		
		sendMessage(new NewRoundMessage());
		currentlyDrawingPlayer.sendMessage(new NewWordMessage(currentWord));
		sendMessageToAllBut(currentlyDrawingPlayer, new StartGuessingMessage(currentlyDrawingPlayer));
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
	
	@Override
	@SuppressWarnings("incomplete-switch")
	public void tick() {
		switch(roomState) {
		case WAIT_BEFORE_NEW_ROUND:
			if (ticks.getAndIncrement() >= 5) {
				startNewRound();
			}
			break;
			
		case ROUND_IN_PROGRESS:
			int elapsed = ticks.getAndIncrement();
		
			if (elapsed <= 60) {
				if (elapsed > 0 && elapsed % 15 == 0) {
					sendMessage(new TickMessage(elapsed));
				}
				
				if (elapsed == 60) {
					roundComplete();
				}
			}
		}
	}

	private synchronized void roundComplete() {
		roomState = RoomState.WAIT_BEFORE_NEW_ROUND;
		ticks.set(0);
		
		sendMessage(new RoundCompleteMessage(currentWord));
		sendMessage(createScoresMessage());
	}

	private ScoresMessage createScoresMessage() {
		ScoresMessage scoresMsg = new ScoresMessage();
		for (Player p : players) {
			scoresMsg.add(p.getName(), p.getScore());
		}
		return scoresMsg;
	}
	
	public boolean canGuess(Player player) {
		return isRoundInProgress() && !isCurrentlyDrawing(player);
	}	
	
	public boolean canDraw(Player player) {
		return isRoundInProgress() && isCurrentlyDrawing(player);
	}
	
	private boolean isCurrentlyDrawing(Player player) {
		return player.equals(currentlyDrawingPlayer);
	}
	
}

enum RoomState {
	WAIT_FOR_PLAYERS, ROUND_IN_PROGRESS, WAIT_BEFORE_NEW_ROUND
}
