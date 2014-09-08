package com.sankar.drawguess;

import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GuessMessage;

public interface IPlayer extends EndPoint {

	String getName();

	void joinRoom(IRoom room);

	void leaveCurrentRoom();

	void guessed(GuessMessage guess);

	void drew(DrawingMessage drawing);

}