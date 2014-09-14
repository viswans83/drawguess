package com.sankar.drawguess.mocks;

import java.util.HashSet;
import java.util.Set;

import com.sankar.drawguess.api.IPlayer;
import com.sankar.drawguess.api.IRound;
import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GuessMessage;

public class MockRound implements IRound {

	@Override
	public void tick() {
		// Ignore
	}
	
	private boolean roundStarted; 

	@Override
	public void start() {
		roundStarted = true;
	}
	
	public boolean didStart() {
		return roundStarted;
	}
	
	private Set<IPlayer> guessed = new HashSet<>();

	@Override
	public void handleGuess(IPlayer player, GuessMessage guessMessage) {
		guessed.add(player);
	}
	
	public boolean didRecieveGuessFrom(IPlayer player) {
		return guessed.contains(player);
	}
	
	private Set<IPlayer> drew = new HashSet<>();

	@Override
	public void handleDrawing(IPlayer player, DrawingMessage drawingMessage) {
		drew.add(player);
	}
	
	public boolean didRecieveDrawingFrom(IPlayer player) {
		return drew.contains(player);
	}
	
	private Set<IPlayer> drawingsWereSentTo = new HashSet<>();

	@Override
	public void sendDrawingsTo(IPlayer player) {
		drawingsWereSentTo.add(player);
	}
	
	public boolean wereDrawingsSentTo(IPlayer player) {
		return drawingsWereSentTo.contains(player);
	}
	
	private Set<IPlayer> quit = new HashSet<>();

	@Override
	public void playerQuit(IPlayer player) {
		quit.add(player);
	}
	
	public boolean didRecieveQuitOf(IPlayer player) {
		return quit.contains(player);
	}
	
	private boolean cancelled;

	@Override
	public void cancel() {
		cancelled = true;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}

}
