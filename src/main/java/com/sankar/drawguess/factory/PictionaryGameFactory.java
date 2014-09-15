package com.sankar.drawguess.factory;

import java.util.Set;

import com.google.inject.Inject;
import com.sankar.drawguess.DefaultPlayerSelector;
import com.sankar.drawguess.DefaultWordProvider;
import com.sankar.drawguess.Game;
import com.sankar.drawguess.Round;
import com.sankar.drawguess.Scores;
import com.sankar.drawguess.api.IGame;
import com.sankar.drawguess.api.IGameFactory;
import com.sankar.drawguess.api.IPlayer;
import com.sankar.drawguess.api.IPlayerSelector;
import com.sankar.drawguess.api.IRoom;
import com.sankar.drawguess.api.IRound;
import com.sankar.drawguess.api.IRoundFactory;
import com.sankar.drawguess.api.IScores;
import com.sankar.drawguess.api.ITimer;
import com.sankar.drawguess.api.IWordProvider;

public class PictionaryGameFactory implements IGameFactory {
	
	private ITimer timer;
	
	@Inject
	public PictionaryGameFactory(ITimer timer) {
		this.timer = timer;
	}
	
	@Override
	public IGame create(Set<IPlayer> players, IRoom room) {
		IScores scores = new Scores(players);
		IWordProvider wordProvider = new DefaultWordProvider();
		IPlayerSelector playerSelector = new DefaultPlayerSelector(room, players);
		IRoundFactory roundFactory = new IRoundFactory() {
			@Override
			public IRound create(String word, IPlayer pictorist, IGame game) {
				return new Round(word, pictorist, game, timer);
			}
		};
		
		return new Game(players, scores, wordProvider, playerSelector, roundFactory, room);
	}

}
