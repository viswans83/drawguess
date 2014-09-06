package com.sankar.drawguess;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sankar.drawguess.msg.AwardMessage;
import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.EmptyRoomMessage;
import com.sankar.drawguess.msg.GameInProgressMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.Message;
import com.sankar.drawguess.msg.NewGameMessage;
import com.sankar.drawguess.msg.NewRoundMessage;
import com.sankar.drawguess.msg.NewWordMessage;
import com.sankar.drawguess.msg.PlayerJoinedMessage;
import com.sankar.drawguess.msg.PlayerQuitMessage;
import com.sankar.drawguess.msg.RoundCompleteMessage;
import com.sankar.drawguess.msg.StartGuessingMessage;
import com.sankar.drawguess.msg.TickMessage;
import com.sankar.drawguess.msg.WordGuessedMessage;

class Room implements EndPoint, Timed {
	
	private static Logger log = LogManager.getLogger();
	
	private String name;
	
	private List<Player> players = new ArrayList<>();
	private List<Player> spectators = new ArrayList<>();
	private List<Player> guessed = new ArrayList<>();
	
	private Scores scores;
	
	private String currentWord;
	private volatile Player currentlyDrawingPlayer;
	
	private WordProvider wordProvider;
	private PlayerSelector playerSelector;
	
	private List<DrawingMessage> drawingsInRound = new ArrayList<>();
	
	private volatile RoomState roomState = RoomState.WAIT_FOR_PLAYERS;
	private AtomicInteger ticks = new AtomicInteger();
	
	public Room(String name, WordProvider wordProvider) {
		this.name = name;
		this.wordProvider = wordProvider;
	}
	
	public String getName() {
		return name;
	}
	
	public synchronized boolean isPresent(Player player) {
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
		
		switch (players.size()) {
		case 0: 
			players.add(player);
			player.sendMessage(new EmptyRoomMessage());
			log.info("Player [{}] joined empty room [{}]", player.getName(), getName());
			break;
		
		case 1:
			players.add(player);
			sendMessageToAllBut(player, new PlayerJoinedMessage(player.getName()));
			log.info("Player [{}] joined room [{}] containing one waiting player", player.getName(), getName());
			startNewGame = true;
			break;
		
		default:
			sendMessageToAllBut(player, new PlayerJoinedMessage(player.getName()));
			if (roomState == RoomState.ROUND_IN_PROGRESS) {
				spectators.add(player);
				player.sendMessage(new GameInProgressMessage());
				log.info("Player [{}] joined room [{}] having an in-progress game", player.getName(), getName());
			}
			else {
				players.add(player);
				log.info("Player [{}] joined room [{}]", player.getName(), getName());
			}
			sendDrawings(player);
		}
		
		if (startNewGame) { 
			startNewGame();
		}
	}
	
	public synchronized void playerQuit(Player player) {
		boolean playerLeft = players.remove(player);
		boolean spectatorLeft = spectators.remove(player);
		
		log.info("Player [{}] quit room [{}]", player.getName(), getName());
		sendMessage(new PlayerQuitMessage(player.getName()));
		
		if (playerLeft && roomState == RoomState.ROUND_IN_PROGRESS) {
			if (players.size() == 1) {
				log.info("There are no remaining players to continue the current in-progress game in room [{}]", getName());
				if (spectators.size() > 0) {
					log.info("There are spectators in room [{}], starting a new game", getName());
					startNewGame();
				}
				else {
					sendMessage(new EmptyRoomMessage());
					roomState = RoomState.WAIT_FOR_PLAYERS;
				}
			}
		}
	}

	private void sendDrawings(Player player) {
		for (Message drawing : drawingsInRound) {
			player.sendMessage(drawing);
		}
	}
	
	public synchronized void playerGuessed(GuessMessage message, Player player) {
		if (message.getGuess().equalsIgnoreCase(currentWord) && !didAlreadyGuess(player)) {
			log.info("Player [{}] guessed the word", player.getName());
			
			guessed.add(player);
			sendMessageToAllBut(player, new WordGuessedMessage(player));
			
			scores.award(currentlyDrawingPlayer, 10);
			currentlyDrawingPlayer.sendMessage(new AwardMessage(10));
			
			scores.award(player, 10);
			player.sendMessage(new AwardMessage(10));
			
			scores.transmit(this);
		}
		else sendMessageToAllBut(player, message);
	}
	
	public synchronized void playerDrew(DrawingMessage drawing) {
		drawingsInRound.add(drawing);
		sendMessageToAllBut(currentlyDrawingPlayer, drawing);
	}
	
	@Override
	public synchronized void sendMessage(Message message) {
		for (Player p : players)
			p.sendMessage(message);
		
		for (Player p : spectators)
			p.sendMessage(message);
	}
	
	public synchronized void sendMessageToAllBut(Player player, Message message) {
		for (Player p : players)
			if (!p.equals(player))
				p.sendMessage(message);
		
		for (Player p : spectators)
			p.sendMessage(message);
	}
	
	private synchronized void startNewGame() {
		log.info("Starting a new game in room [{}]", getName());
		
		players.addAll(spectators);
		spectators.clear();
		
		playerSelector = new DefaultPlayerSelector(this, players);
		scores = new Scores(players);
		
		sendMessage(new NewGameMessage());
		scores.transmit(this);
		startNewRound();
	}
	
	private synchronized void startNewRound() {
		log.info("Starting a new round in room [{}]", getName());
		
		roomState = RoomState.ROUND_IN_PROGRESS;
		ticks.set(0);
		
		drawingsInRound.clear();
		guessed.clear();
		
		if (!playerSelector.hasMorePlayers()) {
			scores.transmit(this);
			startNewGame();
			return;
		}
		
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
		currentlyDrawingPlayer = playerSelector.nextPlayer();
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
		scores.transmit(this);
	}
	
	public boolean canGuess(Player player) {
		return isRoundInProgress() && !isSpectating(player) && !isCurrentlyDrawing(player);
	}	
	
	public boolean canDraw(Player player) {
		return isRoundInProgress() && isCurrentlyDrawing(player);
	}
	
	private boolean isCurrentlyDrawing(Player player) {
		return player.equals(currentlyDrawingPlayer);
	}
	
	private boolean isSpectating(Player player) {
		return spectators.contains(player);
	}
	
	private boolean didAlreadyGuess(Player player) {
		return guessed.contains(player);
	}
	
}

enum RoomState {
	WAIT_FOR_PLAYERS, ROUND_IN_PROGRESS, WAIT_BEFORE_NEW_ROUND
}
