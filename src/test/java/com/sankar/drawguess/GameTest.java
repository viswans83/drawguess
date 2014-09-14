package com.sankar.drawguess;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sankar.drawguess.api.IPlayer;
import com.sankar.drawguess.mocks.MockMessage;
import com.sankar.drawguess.mocks.MockPlayer;
import com.sankar.drawguess.mocks.MockPlayerSelector;
import com.sankar.drawguess.mocks.MockRoom;
import com.sankar.drawguess.mocks.MockRound;
import com.sankar.drawguess.mocks.MockRoundFactory;
import com.sankar.drawguess.mocks.MockScores;
import com.sankar.drawguess.mocks.MockWordProvider;
import com.sankar.drawguess.msg.AwardMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.LineDrawingMessage;
import com.sankar.drawguess.msg.NewGameMessage;

public class GameTest {
	
	private MockPlayer player1;
	private MockPlayer player2;
	private MockPlayer player3;
	
	private Set<IPlayer> players;
	private MockScores scores;
	private MockWordProvider wordProvider;
	private MockPlayerSelector playerSelector;
	private MockRound round;
	private MockRoundFactory roundFactory;
	private MockRoom room;
	
	private Game game;
	
	@Before
	public void before() {
		player1 = new MockPlayer("player1");
		player2 = new MockPlayer("player2");
		player3 = new MockPlayer("player3");
		
		players = new HashSet<>();
		players.add(player1);
		players.add(player2);
		players.add(player3);
		
		scores = new MockScores();
		wordProvider = new MockWordProvider();
		playerSelector = new MockPlayerSelector(player1, player2);
		round = new MockRound();
		roundFactory = new MockRoundFactory(round);
		room = new MockRoom("room");
		
		game = new Game(players, scores, wordProvider, playerSelector, roundFactory, room);
	}
	
	@After
	public void after() {
		player1 = player2 = null;
		
		players = null;
		scores = null;
		wordProvider = null;
		playerSelector = null;
		roundFactory = null;
		room = null;
		
		game = null;
	}
	
	@Test
	public void testNewGameMessageSentToRoomOnStart() {
		game.start();
		
		Assert.assertTrue("NewGameMessage should be sent to the room on start of game", room.didRecieveMessageOfType(NewGameMessage.class));
	}
	
	@Test
	public void testWordIsSelectedOnStart() {
		game.start();
		
		Assert.assertEquals("A new word should be selected when a game begins", 1, wordProvider.countOfWordsWorvided());
	}
	
	@Test
	public void testPictoristIsSelectedOnStart() {
		game.start();
		
		Assert.assertEquals("A player should be selected to draw when a game begins", 1, playerSelector.countOfPlayersSelected());
	}
	
	@Test
	public void testScoresSentToRoomOnStart() {
		game.start();
		
		Assert.assertTrue("Scores should be sent to the room when a game starts", scores.didTransmitTo(room));
	}
	
	@Test
	public void testNewRoundIsCreatedOnStart() {
		game.start();
		
		Assert.assertEquals("A new round should be created when game starts", 1, roundFactory.countOfRoundsCreated());
	}
	
	@Test
	public void testNewRoundStartWhenGameStart() {
		game.start();
		
		Assert.assertTrue("A new round should be started when game starts", round.didStart());
	}
	
	@Test
	public void testScoresTransmittedWhenPlayerJoins() {
		MockPlayer p = new MockPlayer("player3");
		
		game.playerJoined(p);
		
		Assert.assertTrue("When a player joins a game, scores should be sent to the new player", scores.didTransmitTo(p));
	}
	
	@Test
	public void testDrawingsSentWhenPlayerJoinsAndActiveRound() {
		MockPlayer p = new MockPlayer("player3");
		
		game.start();
		game.playerDrew(new LineDrawingMessage(), player2);
		
		game.playerJoined(p);
		
		Assert.assertTrue("When a player joins a game and a round is active, drawings should be sent to the new player", round.wereDrawingsSentTo(p));
	}
	
	@Test
	public void testPlayerRemovedFromGameOnQuit() {
		game.playerQuit(player1);
		
		Assert.assertFalse("Player should be removed from game when he quits", game.isParticipating(player1));
	}
	
	@Test
	public void testPlayerRemovedFromRoundWhenQuits() {
		game.start();
		game.playerQuit(player3);
		
		Assert.assertTrue("Active Round should be notified if player quits", round.didRecieveQuitOf(player3));
	}
	
	@Test
	public void testRoundIsCancelledWhenInsufficientPlayers() {
		game.start();
		game.playerQuit(player3);
		game.playerQuit(player2);
		
		Assert.assertTrue("Active round should be cancelled if number of players are insufficient to continue the round", round.isCancelled());
	}
	
	@Test
	public void testScoresAreSentToRoomWhenRoundIsCancelledDueToInsufficientPlayers() {
		game.start();
		game.playerQuit(player3);
		
		scores.clearSentTo();
		game.playerQuit(player2);
		
		Assert.assertTrue("Scores should be sent to the room when an Active round is cancelled due to insufficient players", scores.didTransmitTo(room));
	}
	
	@Test
	public void testGameOverOnRoomWhenRoundIsCancelledDueToInsufficientPlayers() {
		game.start();
		game.playerQuit(player3);
		
		scores.clearSentTo();
		game.playerQuit(player2);
		
		Assert.assertTrue("Scores should be sent to the room when an Active round is cancelled due to insufficient players", room.didGameEnd());
	}
	
	@Test
	public void testScoresAreTracked() {
		game.start();
		game.award(player1, 10);
		
		Assert.assertEquals("Scores awarded to player should be tracked", 10, scores.getPoints(player1));
	}
	
	@Test
	public void testPlayerIsNotifiedOfAward() {
		game.start();
		game.award(player1, 10);
		
		Assert.assertTrue("Player should be notified of awards", player1.didRecieveMessageOfType(AwardMessage.class));
	}
	
	@Test
	public void testMessagesAreSentToRoom() {
		game.sendMessage(new MockMessage());
		
		Assert.assertTrue("Room should recieve messages sent to the game", room.didRecieveMessageOfType(MockMessage.class));
	}
	
	@Test
	public void testMessagesAreSentToRoom2() {
		game.sendMessageToAllBut(player1, new MockMessage());
		
		Assert.assertTrue("Room should recieve messages sent to the game", room.didRecieveMessageOfType(MockMessage.class));
	}
	
	@Test
	public void testNewRoundStartsOnRoundComplete() {
		game.roundComplete();
		
		Assert.assertEquals("A new round should be created when a round completes and there are players whose turns to draw are pending", 1, roundFactory.countOfRoundsCreated());
		Assert.assertTrue("A new round should begin when a round completes and there are players whose turns to draw are pending", round.didStart());
	}
	
	@Test
	public void testNewWordSelectedOnRoundComplete() {
		game.roundComplete();
		
		Assert.assertEquals("A new word should be selected if a new round started due to the current round completing", 1, wordProvider.countOfWordsWorvided());
	}
	
	@Test
	public void testNewPlayerSelectedOnRoundComplete() {
		game.roundComplete();
		
		Assert.assertEquals("A new pictorist should be selected if a new round started due to the current round completing", 1, playerSelector.countOfPlayersSelected());
	}
	
	@Test
	public void testGameOverOnRoundCompleteAndNoRemainingPlayerTurns() {
		playerSelector.setNoMorePlayers();
		game.roundComplete();
		
		Assert.assertTrue("Game should be over if a round completes and all players remaining have completed their turns", room.didGameEnd());
	}
	
	@Test
	public void testGuessesFromParticipatingPlayersSentToRound() {
		game.start();
		game.playerGuessed(new GuessMessage(), player1);
		
		Assert.assertTrue("Guesses from a participating player should be sent to the active round", round.didRecieveGuessFrom(player1));
	}
	
	@Test
	public void testGuessesFromNonParticipatingPlayersNotSentToRound() {
		MockPlayer p = new MockPlayer("some player");
		
		game.start();
		game.playerGuessed(new GuessMessage(), p);
		
		Assert.assertFalse("Guesses from a non participating player should not be sent to the active round", round.didRecieveGuessFrom(p));
	}
	
	@Test
	public void testDrawingsFromParticipatingPlayersSentToRound() {
		game.start();
		game.playerDrew(new LineDrawingMessage(), player1);
		
		Assert.assertTrue("Guesses from a participating player should be sent to the active round", round.didRecieveDrawingFrom(player1));
	}
	
}
