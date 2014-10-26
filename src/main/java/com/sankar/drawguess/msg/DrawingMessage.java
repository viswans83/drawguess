package com.sankar.drawguess.msg;

import com.sankar.drawguess.api.IPlayer;

public class DrawingMessage extends IncommingMessage {

	@Override
	public void dispatchTo(IPlayer player) {
		player.draw(this);
	}

}
