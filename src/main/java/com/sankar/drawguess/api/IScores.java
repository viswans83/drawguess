package com.sankar.drawguess.api;


public interface IScores {

	void award(IPlayer player, int points);

	void transmit(IEndPoint endPoint);

}