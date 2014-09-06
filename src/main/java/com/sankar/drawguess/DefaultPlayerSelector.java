package com.sankar.drawguess;

import java.util.ArrayList;
import java.util.List;

class DefaultPlayerSelector implements PlayerSelector {
	
	private Room room;
	private List<Player> players;
	
	private int nextPlayerIndex = 0;
	private boolean noMorePlayers;
	
	public DefaultPlayerSelector(Room room, List<Player> players) {
		this.room = room;
		this.players = new ArrayList<>(players);
	}
	
	@Override
	public boolean hasMorePlayers() {
		calculateNextPlayerIndex();
		
		return !noMorePlayers;
	}
	
	@Override
	public Player nextPlayer() {
		calculateNextPlayerIndex();
		
		if (noMorePlayers)
			throw new IllegalStateException();
		else
			return players.get(nextPlayerIndex++);
	}
	
	@Override
	public void rewind() {
		nextPlayerIndex = 0;
		noMorePlayers = false;
	}
	
	private void calculateNextPlayerIndex() {
		Player player;
		
		if (noMorePlayers) return;
		
		while(nextPlayerIndex < players.size()) {
			player = players.get(nextPlayerIndex);
			if (room.isPresent(player)) {
				return;
			}
			nextPlayerIndex++;
		}
		
		noMorePlayers = true;
	}
	
}