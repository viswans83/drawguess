package com.sankar.drawguess.api;


public interface IRoundFactory {
	IRound create(String word, IPlayer pictorist, IGame game);
}
