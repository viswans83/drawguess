package com.sankar.drawguess.msg;

public class NewWordMessage extends Message {
	
	@SuppressWarnings("unused")
	private String newWord;
	
	public NewWordMessage(String word) {
		this.newWord = word;
	}
	
}
