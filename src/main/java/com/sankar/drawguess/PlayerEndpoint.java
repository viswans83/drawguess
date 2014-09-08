package com.sankar.drawguess;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
import com.sankar.drawguess.api.IRoom;
import com.sankar.drawguess.api.ITimer;
import com.sankar.drawguess.msg.Message;

@ServerEndpoint(
		value = "/ws/{room}/{player}", 
		encoders = {MessageTransformer.class},
		decoders = {MessageTransformer.class},
		configurator = GuiceEndpointConfigurator.class)
public class PlayerEndpoint {
	
	private static Logger log = LogManager.getLogger();
	 
	private static ConcurrentMap<String, IRoom> rooms = new ConcurrentHashMap<>();
	
	private IPlayer player;
	private IRoom room;
	
	private ITimer timer;
	
	@Inject
	public PlayerEndpoint(ITimer timer) {
		this.timer = timer;
	}
	
	@OnOpen
	public void onOpen(Session session, @PathParam("room") String roomName, @PathParam("player") String playerName) {
		session.setMaxIdleTimeout(120 * 1000);
		
		IEndPoint playerEndpoint = createPlayerEndPoint(session, roomName, playerName);
		
		rooms.putIfAbsent(roomName, createNewRoom(roomName));
		
		this.player = new Player(playerName, playerEndpoint);
		this.room = rooms.get(roomName);
		
		player.joinRoom(room);
	}

	private Room createNewRoom(String roomName) {
		log.info("Creating new room named [{}]", roomName);
		return new Room(roomName, timer);
	}	
	
	@OnMessage
	public void onMessage(Message message) {
		if (message.isGuess())
			player.guessed(message.asGuess());
		
		else if (message.isDrawing())
			player.drew(message.asDrawing());
	}
	
	@OnError
	public void onError(Throwable t) {
		log.error("Error on player [{}]'s endpoint: {}", player.getName(), t.getMessage());
	}
	
	@OnClose
	public void onClose() {
		player.leaveCurrentRoom();
	}
	
	private SerializingSendHandler createPlayerEndPoint(Session session, String roomName, String playerName) {
		return new SerializingSendHandler(session, playerName, roomName);
	}
	
}
