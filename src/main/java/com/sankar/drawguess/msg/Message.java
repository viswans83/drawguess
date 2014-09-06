package com.sankar.drawguess.msg;

public abstract class Message {
	
	public boolean isGuess() {
		return (this instanceof GuessMessage);
	}
	
	public GuessMessage asGuess() {
		return (GuessMessage)this;
	}
	
	public boolean isDrawing() {
		return (this instanceof DrawingMessage);
	}
	
	public DrawingMessage asDrawing() {
		return (DrawingMessage)this;
	}
	
}
