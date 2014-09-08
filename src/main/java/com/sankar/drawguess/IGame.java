package com.sankar.drawguess;

import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.Message;

public interface IGame {

	void start();

	void award(IPlayer player, int points);

	void sendMessage(Message message);

	void sendMessageToAllBut(IPlayer player, Message message);

	void roundComplete();

	void playerJoined(IPlayer player);

	void playerGuessed(GuessMessage message, IPlayer player);

	void playerDrew(DrawingMessage drawing, IPlayer player);

	void playerQuit(IPlayer player);

}