package com.sankar.drawguess;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sankar.drawguess.mocks.MockGame;
import com.sankar.drawguess.mocks.MockPlayer;
import com.sankar.drawguess.mocks.MockTimer;
import com.sankar.drawguess.msg.NewRoundMessage;
import com.sankar.drawguess.msg.NewWordMessage;
import com.sankar.drawguess.msg.RoundCompleteMessage;
import com.sankar.drawguess.msg.StartGuessingMessage;
import com.sankar.drawguess.msg.TickMessage;

public class RoundTest {
	
	private String word;
	private MockTimer timer;
	private MockPlayer player;
	private MockGame game;
	private Round round;
	
	@Before
	public void before() {
		word = "Test";
		timer = new MockTimer();
		player = new MockPlayer("Sankar");
		game = new MockGame();
		
		round = new Round(word, player, game, timer);
	}
	
	@After
	public void after() {
		timer = null;
		player = null;
		game = null;
		round = null;
	}
	
	@Test(expected = IllegalStateException.class)
	public void testRoundCannotBeStartedMultipleTimes() {
		round.start();
		round.start();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testRoundCannotBeCancelledMultipleTimes() {
		round.start();
		round.cancel();
		round.cancel();
	}
	
	@Test
	public void testRoundRegistersWithTimerOnStart() {
		round.start();
		
		Assert.assertTrue("Round should register itself with Timer on start of round", timer.isRegistered(round));
	}
	
	@Test
	public void testMessagesSentOnGameStart() {
		round.start();
		timer.tick();
		
		Assert.assertTrue("NewRoundMessage should be sent on start", game.didRecieveMessageOfType(NewRoundMessage.class));
		Assert.assertTrue("StartGuessingMessage should be sent on start", game.didRecieveMessageOfType(StartGuessingMessage.class));
		Assert.assertTrue("TickMessage should be sent on start", game.didRecieveMessageOfType(TickMessage.class));
		Assert.assertEquals("Total messages sent on round start should match", 3, game.totalMessagesRecieved());
		
		Assert.assertTrue("NewWordMessage should be sent on start", player.didRecieveMessageOfType(NewWordMessage.class));
	}
	
	@Test
	public void testRoundCompletesIn60Seconds() {
		round.start();
		timer.tick(60);
		
		Assert.assertTrue("Round should complete in 60 seconds", game.didRoundComplete());
		Assert.assertTrue("Game should be notified when round completes", game.didRecieveMessageOfType(RoundCompleteMessage.class));
	}

}
