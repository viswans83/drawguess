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
import com.sankar.drawguess.api.IWordProvider;
import com.sankar.drawguess.api.factory.IRoundFactory;
import com.sankar.drawguess.msg.AwardMessage;
import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.Message;
import com.sankar.drawguess.msg.NewGameMessage;

public class Game implements IGame {
	
	private static Logger log = LogManager.getLogger();
	
	private Set<IPlayer> players;
	private IScores scores;
	private IWordProvider wordProvider;
	private IPlayerSelector playerSelector;
	private IRoundFactory roundFactory;
	private IRoom room;
	
	private IRound round;
	
	public Game(
			
			Set<IPlayer> players,
			IScores scores,
			IWordProvider wordProvider,
			IPlayerSelector playerSelector,
			IRoundFactory roundFactory,
			IRoom room
			
			) {
		
		this.players = new HashSet<>(players);
		this.scores = scores;
		this.wordProvider = wordProvider;
		this.playerSelector = playerSelector;
		this.roundFactory = roundFactory;
		this.room = room;
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
			scores.transmit(room);
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
		log.info("The current round in room [{}] has completed", room.getName());
		
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
		if (!isParticipatingInThisGame(player)) return;
		
		round.handleGuess(player, message);
	}	

	@Override
	public void playerDrew(DrawingMessage drawing, IPlayer player) {
		if (!isParticipatingInThisGame(player)) return;
		
		round.handleDrawing(player, drawing);
	}
	
	private boolean isParticipatingInThisGame(IPlayer player) {
		return round != null && players.contains(player);
	}
	
	private void startNewRound(String word, IPlayer player) {
		log.info("Starting a new round in room [{}]", room.getName());
		scores.transmit(room);
		
		round = roundFactory.create(word, player, this);
		round.start();
	}	

}
