package com.sankar.drawguess.api;

import javax.websocket.Session;

public interface IPlayerEndPointFactory {

	IEndPoint createPlayerEndPoint(Session session, String roomName,
			String playerName);

}