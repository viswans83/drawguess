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
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.Message;

@ServerEndpoint(
		value = "/ws/{room}/{player}", 
		encoders = {MessageTransformer.class},
		decoders = {MessageTransformer.class},
		configurator = GuiceEndpointConfigurator.class)
public class PlayerEndpoint {
	
	private static Logger log = LogManager.getLogger();
	 
	private static ConcurrentMap<String, Room> rooms = new ConcurrentHashMap<>();
	
	private Player player;
	private Room room;
	
	private Timer timer;
	
	@Inject
	public PlayerEndpoint(Timer timer) {
		this.timer = timer;
	}
	
	@OnOpen
	public void onOpen(Session session, @PathParam("room") String roomName, @PathParam("player") String playerName) {
		session.setMaxIdleTimeout(120 * 1000);
		
		SerializingSendHandler sendHandler = new SerializingSendHandler(session, playerName, roomName);
		WordProvider wordProvider = new WordProvider();
		
		if (rooms.putIfAbsent(roomName, new Room(roomName, wordProvider)) == null) {
			timer.registerInterest(rooms.get(roomName));
		}
		
		this.player = new Player(playerName, sendHandler);
		this.room = rooms.get(roomName);
		
		player.joinRoom(room);
	}
	
	@OnMessage
	public void onMessage(Message message) {
		if (message.isGuess() && room.canGuess(player)) {
			GuessMessage guess = message.asGuess();
			guess.setWhoGuessed(player);
			player.guessed(guess);
		}
		
		else if (message.isDrawing() && room.canDraw(player))
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
	
}
