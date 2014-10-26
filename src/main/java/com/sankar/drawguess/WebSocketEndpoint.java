package com.sankar.drawguess;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.sankar.drawguess.api.IEndPoint;
import com.sankar.drawguess.api.IPlayer;
import com.sankar.drawguess.api.IPlayerEndPointFactory;
import com.sankar.drawguess.api.IRooms;
import com.sankar.drawguess.msg.IncommingMessage;

@ServerEndpoint(
		value = "/ws/{room}/{player}", 
		encoders = {MessageTransformer.class},
		decoders = {MessageTransformer.class},
		configurator = GuiceEndpointConfigurator.class)
public class WebSocketEndpoint {
	
	private static Logger log = LogManager.getLogger();
	
	private IRooms rooms;
	private IPlayerEndPointFactory playerEndpointFactory;
	
	private IPlayer player;
	
	@Inject
	public WebSocketEndpoint(IRooms rooms, IPlayerEndPointFactory playerEndpointFactory) {
		this.rooms = rooms;
		this.playerEndpointFactory = playerEndpointFactory;
	}
	
	@OnOpen
	public void onOpen(Session session, @PathParam("room") String roomName, @PathParam("player") String playerName) {
		session.setMaxIdleTimeout(120 * 1000);
		
		IEndPoint playerEndpoint = playerEndpointFactory.createPlayerEndPoint(session, roomName, playerName);
		
		player = new Player(playerName, playerEndpoint);
		player.joinRoom(rooms.createOrGetExisting(roomName));
	}
	
	@OnMessage
	public void onMessage(IncommingMessage message) {
		message.dispatchTo(player);
	}
	
	@OnError
	public void onError(Throwable t) {
		log.error("Error on player [{}]'s endpoint: {}", player.getName(), t.getMessage());
	}
	
	@OnClose
	public void onClose() {
		player.leaveCurrentRoom();
	}
	
}
