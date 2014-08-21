package com.sankar.drawguess;

import javax.websocket.server.ServerEndpointConfig.Configurator;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceEndpointConfigurator extends Configurator {
	
	private Injector injector = Guice.createInjector(new AbstractModule() {
		@Override
		protected void configure() {
			bind(PlayerEndpoint.class);
		}
	});
	
	@Override
	public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
		return injector.getInstance(endpointClass);
	}
	
}
