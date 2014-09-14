package com.sankar.drawguess.factory;

import com.google.inject.Inject;
import com.sankar.drawguess.Round;
import com.sankar.drawguess.api.IGame;
import com.sankar.drawguess.api.IPlayer;
import com.sankar.drawguess.api.IRound;
import com.sankar.drawguess.api.IRoundFactory;
import com.sankar.drawguess.api.IRoundFactoryProvider;
import com.sankar.drawguess.api.ITimer;

public class PictionaryRoundFactoryProvider implements IRoundFactoryProvider {
	
	private ITimer timer;
	
	@Inject
	public PictionaryRoundFactoryProvider(ITimer timer) {
		this.timer = timer;
	}
	
	@Override
	public IRoundFactory create() {
		return new IRoundFactory() {
			@Override
			public IRound create(String word, IPlayer pictorist, IGame game) {
				return new Round(word, pictorist, game, timer);
			}
		};
	}

}
