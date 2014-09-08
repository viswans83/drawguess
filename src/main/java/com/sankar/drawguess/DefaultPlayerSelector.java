package com.sankar.drawguess;

import java.util.ArrayList;
import java.util.List;

import com.sankar.drawguess.api.IPlayer;
import com.sankar.drawguess.api.IPlayerSelector;
import com.sankar.drawguess.api.IRoom;

class DefaultPlayerSelector implements IPlayerSelector {
	
	private IRoom room;
	private List<IPlayer> players;
	
	private int nextPlayerIndex = 0;
	private boolean noMorePlayers;
	
	public DefaultPlayerSelector(IRoom room, List<IPlayer> players) {
		this.room = room;
		this.players = new ArrayList<>(players);
	}
	
	@Override
	public boolean hasMorePlayers() {
		calculateNextPlayerIndex();
		
		return !noMorePlayers;
	}
	
	@Override
	public IPlayer nextPlayer() {
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
		IPlayer player;
		
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