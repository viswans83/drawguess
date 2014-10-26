package com.sankar.drawguess;

import javax.websocket.server.ServerEndpointConfig.Configurator;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sankar.drawguess.api.IGameFactory;
import com.sankar.drawguess.api.IPlayerEndPointFactory;
import com.sankar.drawguess.api.IRoomFactory;
import com.sankar.drawguess.api.IRooms;
import com.sankar.drawguess.api.ITimer;
import com.sankar.drawguess.factory.PictionaryGameFactory;
import com.sankar.drawguess.factory.PictionaryRoomFactory;

public class GuiceEndpointConfigurator extends Configurator {
	
	private static Injector injector = Guice.createInjector(new AbstractModule() {
		@Override
		protected void configure() {
			bind(ITimer.class).to(Timer.class);
			bind(IPlayerEndPointFactory.class).to(PlayerEndPointFactory.class);
			bind(IRooms.class).to(Rooms.class);
			bind(IGameFactory.class).to(PictionaryGameFactory.class);
			bind(IRoomFactory.class).to(PictionaryRoomFactory.class);
			bind(WebSocketEndpoint.class);
		}
	});
	
	@Override
	public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
		return injector.getInstance(endpointClass);
	}
	
}
