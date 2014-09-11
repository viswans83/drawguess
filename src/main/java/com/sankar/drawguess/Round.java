package com.sankar.drawguess;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sankar.drawguess.api.IGame;
import com.sankar.drawguess.api.IPlayer;
import com.sankar.drawguess.api.IRound;
import com.sankar.drawguess.api.ITimer;
import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.NewRoundMessage;
import com.sankar.drawguess.msg.NewWordMessage;
import com.sankar.drawguess.msg.RoundCancelledMessage;
import com.sankar.drawguess.msg.RoundCompleteMessage;
import com.sankar.drawguess.msg.StartGuessingMessage;
import com.sankar.drawguess.msg.TickMessage;
import com.sankar.drawguess.msg.WordGuessedMessage;

public class Round implements IRound {
	
	public static int TICKS_PER_ROUND = 60;
	
	private String word;
	private IPlayer pictorist;
	private IGame game;
	private ITimer timer;
	
	private Set<IPlayer> playersWhoGuessedCorrectly = new HashSet<>();
	private List<DrawingMessage> drawings = new ArrayList<>();
	
	private RoundState state = RoundState.NOT_STARTED;
	private int timeRemaining = TICKS_PER_ROUND;
	
	public Round(String word, IPlayer pictorist, IGame game, ITimer timer) {
		this.word = word;
		this.pictorist = pictorist;
		this.game = game;
		this.timer = timer;
	}
	
	@Override
	public void start() {
		if (state != RoundState.NOT_STARTED) 
			throw new IllegalStateException();
		
		timer.registerInterest(this);
		state = RoundState.STARTED;
	}
	
	@Override
	public void handleGuess(IPlayer player, GuessMessage guessMessage) {
		String guess = guessMessage.getGuess();
		
		if (shouldIgnoreGuessBy(player)) 
			return;
		
		else if (guessMatchesWord(guess))
			guessedCorrectly(player);
		
		else
			guessedIncorrectly(guessMessage);
	}
	
	@Override
	public void handleDrawing(IPlayer player, DrawingMessage drawingMessage) {
		if (shouldIgnoreDrawingBy(player))
			return;
		
		drawings.add(drawingMessage);
		sendDrawing(drawingMessage);
	}
	
	@Override
	public void playerQuit(IPlayer player) {
		if (player.equals(pictorist))
			roundComplete();
	}
	
	@Override
	public void sendDrawingsTo(IPlayer player) {
		for (DrawingMessage drawing : drawings)
			player.sendMessage(drawing);
	}
	
	@Override
	public void cancel() {
		if (state == RoundState.CANCELLED)
			throw new IllegalStateException();
		
		game.sendMessage(new RoundCancelledMessage(word));
		timer.unregisterInterest(this);
		
		state = RoundState.CANCELLED;
	}
	
	private void sendInitialMessages() {
		game.sendMessage(new NewRoundMessage());
		pictorist.sendMessage(new NewWordMessage(word));
		game.sendMessageToAllBut(pictorist, new StartGuessingMessage(pictorist));
	}

	private void sendDrawing(DrawingMessage drawingMessage) {
		game.sendMessageToAllBut(pictorist, drawingMessage);
	}
	
	private boolean shouldIgnoreGuessBy(IPlayer player) {
		return (player.equals(pictorist) || playersWhoGuessedCorrectly.contains(player));
	}
	
	private boolean guessMatchesWord(String guess) {
		return guess.equalsIgnoreCase(word);
	}
	
	private boolean shouldIgnoreDrawingBy(IPlayer player) {
		return !player.equals(pictorist);
	}
	
	private void guessedCorrectly(IPlayer player) {
		playersWhoGuessedCorrectly.add(player);
		
		game.sendMessage(new WordGuessedMessage(player));
		
		game.award(player, 10);
		game.award(pictorist, 10);
	}
	
	private void guessedIncorrectly(GuessMessage guessMessage) {
		game.sendMessage(guessMessage);
	}
	
	private void roundComplete() {
		timer.unregisterInterest(this);
		state = RoundState.COMPLETED;
		
		game.sendMessage(new RoundCompleteMessage(word));
		game.roundComplete();
	}
	
	private void sendTimeRemaining(int timeRemaining) {
		game.sendMessage(new TickMessage(timeRemaining));
	}
	
	@Override
	public void tick() {
		if (timeRemaining == TICKS_PER_ROUND)
			sendInitialMessages();
		
		if (timeRemaining % 15 == 0)
			sendTimeRemaining(timeRemaining);
		
		if (--timeRemaining == 0) {
			timer.unregisterInterest(this);
			roundComplete();
		}
	}
	
	private enum RoundState { NOT_STARTED, STARTED, CANCELLED, COMPLETED } 

}
