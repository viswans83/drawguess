package com.sankar.drawguess;

import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.Message;

@ServerEndpoint(
		value = "/ws/{room}/{player}", 
		encoders = {MessageTransformer.class},
		decoders = {MessageTransformer.class},
		configurator = GuiceEndpointConfigurator.class)
public class PlayerEndpoint implements EndPoint {
	
	private static Logger log = LogManager.getLogger();
	
	private static ConcurrentMap<String, Room> rooms = new ConcurrentHashMap<>();
	
	private Session session;
	private Player player;
	private Room room;
	
	@Inject
	public PlayerEndpoint() {}
	
	@OnOpen
	public void onOpen(Session session, @PathParam("room") String roomName, @PathParam("player") String playerName) {
		session.setMaxIdleTimeout(120 * 1000);
		
		rooms.putIfAbsent(roomName, new Room(roomName));
		
		this.session = session;
		this.player = new Player(playerName, this);
		this.room = rooms.get(roomName);
		
		room.playerJoined(player);
	}
	
	@OnMessage
	public void onMessage(Message message) {
		if (isValidGuess(message))
			room.playerGuessed(message.asGuess(), player);
		
		else if (isValidDrawing(message))
			room.playerDrew(message.asDrawing());
	}

	private boolean isValidDrawing(Message message) {
		return message instanceof DrawingMessage && room.getCurrentlyDrawingPlayer().equals(player);
	}

	private boolean isValidGuess(Message message) {
		return message instanceof GuessMessage && !room.getCurrentlyDrawingPlayer().equals(player) && message.asGuess().getGuess() != null;
	}
	
	@OnError
	public void onError(Throwable t) {
		log.error("Error on player [{}]'s endpoint: {}", player.getName(), t.getMessage());
	}
	
	@OnClose
	public void onClose() {
		room.playerQuit(player);
	}
	
	@Override
	public void sendMessage(Message message) {
		if (!sending)
			sendInternal(message);
		else
			pendingMessages.add(message);
	}
	
	private void sendInternal(Message message) {
		if (session.isOpen()) {
			sending = true;
			Async async = session.getAsyncRemote();
			async.sendObject(message, messageSentCallback);
		}
	}
	
	private boolean sending = false;
	
	private Deque<Message> pendingMessages = new ConcurrentLinkedDeque<>();
	
	private SendHandler messageSentCallback = new SendHandler() {
		@Override
		public void onResult(SendResult sr) {
			if (!sr.isOK()) {
				log.error("Failed to send a message to player [{}]", player.getName());
			}
			Message message;
			if ((message = pendingMessages.poll()) != null)
				sendInternal(message);
			else
				sending = false;
		}
	};
	
}
