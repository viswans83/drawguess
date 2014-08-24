package com.sankar.drawguess.msg;

public class TickMessage extends Message {
	
	@SuppressWarnings("unused")
	private int ticks;
	
	public TickMessage(int ticks) {
		this.ticks = ticks;
	}

}
