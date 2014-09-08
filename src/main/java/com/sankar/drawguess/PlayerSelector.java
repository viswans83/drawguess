package com.sankar.drawguess;

interface PlayerSelector {
	
	boolean hasMorePlayers();
	
	IPlayer nextPlayer();
	
	void rewind();
	
}