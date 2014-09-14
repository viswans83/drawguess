package com.sankar.drawguess.mocks;

import com.sankar.drawguess.api.IPlayer;
import com.sankar.drawguess.api.IPlayerSelector;

public class MockPlayerSelector implements IPlayerSelector {
	
	private int indx;
	private IPlayer p1, p2;
	
	public MockPlayerSelector(IPlayer player1, IPlayer player2) {
		this.p1 = player1;
		this.p2 = player2;
	}
	
	private boolean more = true;
	
	@Override
	public boolean hasMorePlayers() {
		return more;
	}
	
	public void setNoMorePlayers() {
		this.more = false;
	}

	@Override
	public IPlayer nextPlayer() {
		return (indx++ % 2) == 0 ? p1 : p2;
	}
	
	public int countOfPlayersSelected() {
		return indx;
	}

	@Override
	public void rewind() {
		throw new UnsupportedOperationException();
	}

}
