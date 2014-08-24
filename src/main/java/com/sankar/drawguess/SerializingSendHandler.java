package com.sankar.drawguess;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;
import javax.websocket.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sankar.drawguess.msg.Message;

public class SerializingSendHandler implements EndPoint {
	
	private static Logger log = LogManager.getLogger();
	
	private Session session;
	private String playerName;
	private String roomName;
	
	private AtomicBoolean sending = new AtomicBoolean();
	private Deque<Message> pendingMessages = new ConcurrentLinkedDeque<>();
	
	public SerializingSendHandler(Session session, String playerName, String roomName) {
		this.session = session;
		this.playerName = playerName;
		this.roomName = roomName;
	}
	
	@Override
	public void sendMessage(Message message) {
		if (!sending.compareAndSet(false, true))
			sendInternal(message);
		else
			pendingMessages.add(message);
	}
	
	private void sendInternal(Message message) {
		if (session.isOpen()) {
			Async async = session.getAsyncRemote();
			try {
				async.sendObject(message, messageSentCallback);
			} catch(RuntimeException e) {
				// Not sure why this would happen
			}
		}
		else pendingMessages.clear();
	}
	
	private SendHandler messageSentCallback = new SendHandler() {
		@Override
		public void onResult(SendResult sr) {
			if (!sr.isOK()) {
				log.error("Failed to send a message to player [{}] in room [{}]", playerName, roomName);
			}
			Message message;
			if ((message = pendingMessages.poll()) != null)
				sendInternal(message);
			else
				sending.set(false);
		}
	};
	
}
