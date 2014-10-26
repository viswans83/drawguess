package com.sankar.drawguess.msg;

import com.sankar.drawguess.api.IPlayer;

public abstract class IncommingMessage extends Message {
	
	public abstract void dispatchTo(IPlayer player);

}
