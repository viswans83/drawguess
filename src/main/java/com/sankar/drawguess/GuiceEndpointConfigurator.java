package com.sankar.drawguess;

import javax.websocket.server.ServerEndpointConfig.Configurator;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sankar.drawguess.api.ITimer;

public class GuiceEndpointConfigurator extends Configurator {
	
	private Injector injector = Guice.createInjector(new AbstractModule() {
		@Override
		protected void configure() {
			Timer timer = new Timer();
			timer.start();
			
			bind(PlayerEndpoint.class);
			bind(ITimer.class).toInstance(timer);
		}
	});
	
	@Override
	public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
		return injector.getInstance(endpointClass);
	}
	
}
