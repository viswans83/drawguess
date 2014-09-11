package com.sankar.drawguess.msg;

public class RoundCancelledMessage extends Message {
	@SuppressWarnings("unused")
	private boolean roundCancelled = true;
	
	@SuppressWarnings("unused")
	private String originalWord;
	
	public RoundCancelledMessage(String originalWord) {
		this.originalWord = originalWord;
	}
}
