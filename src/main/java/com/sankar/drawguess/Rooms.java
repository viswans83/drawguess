package com.sankar.drawguess;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sankar.drawguess.api.IRoom;
import com.sankar.drawguess.api.IRoomFactory;
import com.sankar.drawguess.api.IRooms;

@Singleton
public class Rooms implements IRooms {
	
	private static Logger log = LogManager.getLogger();
	
	private IRoomFactory roomFactory;
	private ConcurrentMap<String, IRoom> rooms = new ConcurrentHashMap<>();
	
	@Inject
	public Rooms(IRoomFactory roomFactory) {
		this.roomFactory = roomFactory;
	}
	
	@Override
	public IRoom createOrGetExisting(String roomName) {
		rooms.putIfAbsent(roomName, createNewRoom(roomName));
		return rooms.get(roomName);
	}
	
	private IRoom createNewRoom(String roomName) {
		log.info("Creating new room named [{}]", roomName);
		return roomFactory.create(roomName);
	}

}
