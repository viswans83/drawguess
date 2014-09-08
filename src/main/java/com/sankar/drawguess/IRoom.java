package com.sankar.drawguess;

import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.Message;

interface IRoom extends EndPoint {

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