package com.sankar.drawguess.mocks;

import java.util.Set;

import com.sankar.drawguess.api.IGame;
import com.sankar.drawguess.api.IGameFactory;
import com.sankar.drawguess.api.IPlayer;
import com.sankar.drawguess.api.IRoom;

public class MockGameFactory implements IGameFactory {
	
	private int gamesCreated;
	private MockGame game;

	@Override
	public IGame create(Set<IPlayer> players, IRoom room) {
		gamesCreated++;
		return (game = new MockGame());
	}
	
	public MockGame getGame() {
		return game;
	}
	
	public int countOfGamesCreated() {
		return gamesCreated;
	}

}
