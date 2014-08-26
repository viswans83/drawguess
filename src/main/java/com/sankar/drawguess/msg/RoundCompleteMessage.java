package com.sankar.drawguess.msg;

public class RoundCompleteMessage extends Message {
	
	@SuppressWarnings("unused")
	private boolean roundComplete = true;
	
	@SuppressWarnings("unused")
	private String originalWord;
	
	public RoundCompleteMessage(String originalWord) {
		this.originalWord = originalWord;
	}

}
