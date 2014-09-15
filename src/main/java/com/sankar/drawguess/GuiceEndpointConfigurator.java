package com.sankar.drawguess;

import javax.websocket.server.ServerEndpointConfig.Configurator;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sankar.drawguess.api.IGameFactory;
import com.sankar.drawguess.api.IRoomFactory;
import com.sankar.drawguess.api.ITimer;
import com.sankar.drawguess.factory.PictionaryGameFactory;
import com.sankar.drawguess.factory.PictionaryRoomFactory;

public class GuiceEndpointConfigurator extends Configurator {
	
	private Injector injector = Guice.createInjector(new AbstractModule() {
		@Override
		protected void configure() {
			Timer timer = new Timer();
			timer.start();
			
			bind(ITimer.class).toInstance(timer);
			bind(IGameFactory.class).to(PictionaryGameFactory.class);
			bind(IRoomFactory.class).to(PictionaryRoomFactory.class);
			bind(PlayerEndpoint.class);
		}
	});
	
	@Override
	public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
		return injector.getInstance(endpointClass);
	}
	
}
