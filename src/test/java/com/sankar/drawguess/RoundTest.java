package com.sankar.drawguess;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sankar.drawguess.api.IPlayer;
import com.sankar.drawguess.mocks.MockGame;
import com.sankar.drawguess.mocks.MockPlayer;
import com.sankar.drawguess.mocks.MockTimer;
import com.sankar.drawguess.msg.DrawingMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.NewRoundMessage;
import com.sankar.drawguess.msg.NewWordMessage;
import com.sankar.drawguess.msg.RoundCancelledMessage;
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
	public void testRoundShouldCompleteOnCancel() {
		round.start();
		round.cancel();
		
		Assert.assertTrue("RoundCancelledMessage should be sent on round cancel", game.didRecieveMessageOfType(RoundCancelledMessage.class));
	}
	
	@Test
	public void testRoundShouldUnregisterWithTimerOnCancel() {
		round.start();
		round.cancel();
		
		Assert.assertFalse("Round should unregister with Timer on cancel", timer.isRegistered(round));
	}
	
	@Test
	public void testRoundShouldCompleteIfPictoristQuits() {
		round.start();
		round.playerQuit(player);
		
		Assert.assertTrue("Round should complete if pictorist quits", game.didRoundComplete());
	}
	
	@Test
	public void testDrawingsShouldBeSentIfPictoristDraws() {
		round.start();
		
		round.handleDrawing(player, new DrawingMessage());
		round.handleDrawing(player, new DrawingMessage());
		
		Assert.assertEquals("Game should recieve all drawings made", 2, game.totalMessagesRecievedOfType(DrawingMessage.class));
	}
	
	@Test
	public void testOutOfTurnDrawingsShouldNotBeSent() {
		round.start();
		
		IPlayer p = new MockPlayer("other player");
		
		round.handleDrawing(p, new DrawingMessage());
		round.handleDrawing(p, new DrawingMessage());
		
		Assert.assertEquals("Game should not recieve drawings made by players other than pictorist", 0, game.totalMessagesRecievedOfType(DrawingMessage.class));
	}
	
	@Test
	public void testDrawingsInRoundShouldBeSentToPlayer() {
		round.handleDrawing(player, new DrawingMessage());
		round.handleDrawing(player, new DrawingMessage());
		
		MockPlayer p = new MockPlayer("other player");
		round.sendDrawingsTo(p);
		
		Assert.assertEquals("Player should recieve all recorded drawings", 2, p.countRecievedMessageOfType(DrawingMessage.class));
	}
	
	@Test
	public void testIncorrectGuessesShouldBeSent() {
		IPlayer p = new MockPlayer("other player");
		
		round.start();
		round.handleGuess(p, new GuessMessage("wrong"));
		
		Assert.assertEquals("Game should recieve incorrect guess", 1, game.totalMessagesRecievedOfType(GuessMessage.class));
	}
	
	@Test
	public void testGuessesShouldNotBeSent() {
		IPlayer p = new MockPlayer("other player");
		
		round.start();
		round.handleGuess(p, new GuessMessage("test"));
		round.handleGuess(player, new GuessMessage("wrong"));
		
		Assert.assertEquals("Game should not recieve correct guess", 0, game.totalMessagesRecievedOfType(GuessMessage.class));
	}
	
	@Test
	public void testGuesserAwardOnCorrectGuess() {
		IPlayer p = new MockPlayer("other player");
		
		round.start();
		round.handleGuess(p, new GuessMessage("test"));
		
		Assert.assertEquals("Guesser should be awarded for correct guesses", 10, game.score(p));
	}
	
	@Test
	public void testPictoristAwardOnCorrectGuess() {
		IPlayer p = new MockPlayer("other player");
		
		round.start();
		round.handleGuess(p, new GuessMessage("test"));
		
		Assert.assertEquals("Pictorist should be awarded for correct guesses", 10, game.score(player));
	}
	
	@Test
	public void testRoundRegistersWithTimerOnStart() {
		round.start();
		
		Assert.assertTrue("Round should register itself with Timer on start of round", timer.isRegistered(round));
	}
	
	@Test
	public void testNewRoundMessageShouldBeSentToGameOnRoundStart() {
		round.start();
		timer.tick();
		
		Assert.assertTrue("NewRoundMessage should be sent on start", game.didRecieveMessageOfType(NewRoundMessage.class));
	}
	
	@Test
	public void testStartGuessingMessageShouldBeSentToGameOnRoundStart() {
		round.start();
		timer.tick();
		
		Assert.assertTrue("StartGuessingMessage should be sent on start", game.didRecieveMessageOfType(StartGuessingMessage.class));
	}
	
	@Test
	public void testTickMessageShouldBeSentToGameOnRoundStart() {
		round.start();
		timer.tick();
		
		Assert.assertTrue("TickMessage should be sent on start", game.didRecieveMessageOfType(TickMessage.class));
	}
	
	@Test
	public void testTotalMessagesSentToGameOnRoundStart() {
		round.start();
		timer.tick();
		
		Assert.assertEquals("Total messages sent on round start should match", 3, game.totalMessagesRecieved());
	}
	
	@Test
	public void testNewWordMessageSentToPlayerOnRoundStart() {
		round.start();
		timer.tick();
		
		Assert.assertTrue("NewWordMessage should be sent on start", player.didRecieveMessageOfType(NewWordMessage.class));
	}
	
	@Test
	public void testTicksSentEvery15Seconds() {
		round.start();
		timer.tick(46);
		
		Assert.assertEquals("4 TickMessages should have been sent at the end of 45 seconds", 4, game.totalMessagesRecievedOfType(TickMessage.class));
	}
	
	@Test
	public void testRoundCompletesIn60Seconds() {
		round.start();
		timer.tick(61);
		
		Assert.assertTrue("Round should complete at end of 60 seconds", game.didRoundComplete());
	}
	
	@Test
	public void testGameNotifiedOnRoundComplete() {
		round.start();
		timer.tick(61);
		
		Assert.assertTrue("Game should be notified when round completes", game.didRecieveMessageOfType(RoundCompleteMessage.class));
	}
	
	@Test
	public void testRoundUnRegistersWithTimerOnRoundComplete() {
		round.start();
		timer.tick(61);
		
		Assert.assertFalse("Round should unregister itself with Timer on round completion", timer.isRegistered(round));
	}

}
