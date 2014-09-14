package com.sankar.drawguess.factory;

import com.google.inject.Inject;
import com.sankar.drawguess.Room;
import com.sankar.drawguess.api.IGameFactory;
import com.sankar.drawguess.api.IRoom;
import com.sankar.drawguess.api.IRoomFactory;

public class PictionaryRoomFactory implements IRoomFactory {
	
	private IGameFactory gameFactory;
	
	@Inject
	public PictionaryRoomFactory(IGameFactory gameFactory) {
		this.gameFactory = gameFactory;
	}

	@Override
	public IRoom create(String name) {
		return new Room(name, gameFactory);
	}

}
