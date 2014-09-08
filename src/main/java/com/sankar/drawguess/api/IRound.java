package com.sankar.drawguess.api;

import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GuessMessage;

public interface IRound {

	void start();

	void handleGuess(IPlayer player, GuessMessage guessMessage);

	void handleDrawing(IPlayer player, DrawingMessage drawingMessage);

	void sendDrawingsTo(IPlayer player);

	void playerQuit(IPlayer player);

	void cancel();

}