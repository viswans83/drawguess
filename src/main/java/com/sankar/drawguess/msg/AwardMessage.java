package com.sankar.drawguess.msg;

public class AwardMessage extends Message {
	
	@SuppressWarnings("unused")
	private int award;
	
	public AwardMessage(int points) {
		this.award = points;
	}
	
}
