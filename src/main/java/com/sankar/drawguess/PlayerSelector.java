package com.sankar.drawguess;

interface PlayerSelector {
	
	boolean hasMorePlayers();
	
	Player nextPlayer();
	
	void rewind();
	
}