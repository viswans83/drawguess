package com.sankar.drawguess.api;

import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GuessMessage;

public interface IPlayer extends IEndPoint {

	String getName();

	void joinRoom(IRoom room);

	void leaveCurrentRoom();

	void guessed(GuessMessage guess);

	void drew(DrawingMessage drawing);

}