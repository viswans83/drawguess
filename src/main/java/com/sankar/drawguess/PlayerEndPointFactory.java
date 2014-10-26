package com.sankar.drawguess;

import javax.websocket.Session;

import com.google.inject.Singleton;
import com.sankar.drawguess.api.IEndPoint;
import com.sankar.drawguess.api.IPlayerEndPointFactory;

@Singleton
public class PlayerEndPointFactory implements IPlayerEndPointFactory {
	
	@Override
	public IEndPoint createPlayerEndPoint(Session session, String roomName, String playerName) {
		return new SerializingEndPoint(session, playerName, roomName);
	}

}
