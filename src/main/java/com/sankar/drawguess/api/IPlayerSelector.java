package com.sankar.drawguess.api;

public interface IPlayerSelector {
	
	boolean hasMorePlayers();
	
	IPlayer nextPlayer();
	
	void rewind();
	
}