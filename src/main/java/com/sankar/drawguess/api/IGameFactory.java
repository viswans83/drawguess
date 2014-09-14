package com.sankar.drawguess.api;

import java.util.Set;

public interface IGameFactory {
	IGame create(Set<IPlayer> players, IRoom room);
}
