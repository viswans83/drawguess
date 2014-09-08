package com.sankar.drawguess;

import java.util.ArrayList;
import java.util.List;

import com.sankar.drawguess.msg.AwardMessage;
import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.Message;
import com.sankar.drawguess.msg.NewGameMessage;

public class Game implements IGame {
	
	private List<IPlayer> players = new ArrayList<>();
	private IRoom room;
	private Timer timer;
	
	private Scores scores;
	private WordProvider wordProvider;
	private PlayerSelector playerSelector;
	
	private IRound round;
	
	public Game(List<IPlayer> playersThisInstant, IRoom room, Timer timer) {
		this.players = new ArrayList<>(playersThisInstant);
		this.room = room;
		this.timer = timer;
		
		this.scores = new Scores(playersThisInstant);
		this.wordProvider = new WordProvider();
		this.playerSelector = new DefaultPlayerSelector(room, playersThisInstant);
	}
	
	@Override
	public void start() {
		room.sendMessage(new NewGameMessage());
		
		String word = wordProvider.nextWord();
		IPlayer nextPlayer = playerSelector.nextPlayer();
		
		startNewRound(word, nextPlayer);
	}
	
	@Override
	public void playerJoined(IPlayer player) {
		if (round == null) return;
		
		scores.transmit(player);
		round.sendDrawingsTo(player);
	}
	
	@Override
	public void playerQuit(IPlayer player) {
		players.remove(player);
		
		if (players.size() < 2) {
			round.cancel();
			room.gameOver();
		}
		
		else if (round != null)
			round.playerQuit(player);
	}
	
	@Override
	public void award(IPlayer player, int points) {
		scores.award(player, points);
		player.sendMessage(new AwardMessage(points));
	}
	
	@Override
	public void sendMessage(Message message) {
		room.sendMessage(message);
	}
	
	@Override
	public void sendMessageToAllBut(IPlayer player, Message message) {
		room.sendMessageToAllBut(player, message);
	}
	
	@Override
	public void roundComplete() {
		if (playerSelector.hasMorePlayers()) {
			String word = wordProvider.nextWord();
			IPlayer nextPlayer = playerSelector.nextPlayer();
			
			startNewRound(word, nextPlayer);
		}
		
		else {
			scores.transmit(room);
			room.gameOver();
		}
	}

	@Override
	public void playerGuessed(GuessMessage message, IPlayer player) {
		if (round == null) return;
		
		round.handleGuess(player, message);
	}

	@Override
	public void playerDrew(DrawingMessage drawing, IPlayer player) {
		if (round == null) return;
		
		round.handleDrawing(player, drawing);
	}
	
	private void startNewRound(String word, IPlayer player) {
		scores.transmit(room);
		
		round = new Round(word, player, this, timer);
		round.start();
	}	

}
