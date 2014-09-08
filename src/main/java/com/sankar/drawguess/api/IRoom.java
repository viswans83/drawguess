package com.sankar.drawguess.api;

import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.Message;

public interface IRoom extends IEndPoint {

	public static final int MIN_PLAYERS_PER_GAME = 3;

	String getName();

	void playerJoined(IPlayer player);

	void playerQuit(IPlayer player);

	void playerGuessed(GuessMessage message, IPlayer player);

	void playerDrew(DrawingMessage drawing, IPlayer player);

	void sendMessageToAllBut(IPlayer player, Message message);

	void gameOver();

	boolean isPresent(IPlayer player);

}