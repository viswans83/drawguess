package com.sankar.drawguess.msg;

public abstract class Message {
	
	public GuessMessage asGuess() {
		return (GuessMessage)this;
	}
	
	public DrawingMessage asDrawing() {
		return (DrawingMessage)this;
	}

	public FloodFillMessage asFloodFill() {
		return (FloodFillMessage)this;
	}
	
}
