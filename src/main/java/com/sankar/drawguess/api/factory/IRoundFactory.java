package com.sankar.drawguess.api.factory;

import com.sankar.drawguess.api.IGame;
import com.sankar.drawguess.api.IPlayer;
import com.sankar.drawguess.api.IRound;

public interface IRoundFactory {
	IRound create(String word, IPlayer pictorist, IGame game);
}
