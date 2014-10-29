package com.sankar.drawguess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

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
	
	@Test
	public void testNewGameMessageSentToRoomOnStart() {
		game.start();
		
		assertTrue(room.didRecieveMessageOfType(NewGameMessage.class));
	}
	
	@Test
	public void testWordIsSelectedOnStart() {
		game.start();
		
		assertEquals(1, wordProvider.countOfWordsWorvided());
	}
	
	@Test
	public void testPictoristIsSelectedOnStart() {
		game.start();
		
		assertEquals(1, playerSelector.countOfPlayersSelected());
	}
	
	@Test
	public void testScoresSentToRoomOnStart() {
		game.start();
		
		assertTrue("Scores should be sent to the room when a game starts", scores.didTransmitTo(room));
	}
	
	@Test
	public void testNewRoundIsCreatedOnStart() {
		game.start();
		
		assertEquals(1, roundFactory.countOfRoundsCreated());
	}
	
	@Test
	public void testNewRoundStartWhenGameStart() {
		game.start();
		
		assertTrue(round.didStart());
	}
	
	@Test
	public void testScoresTransmittedWhenPlayerJoins() {
		MockPlayer p = new MockPlayer("player3");
		
		game.playerJoined(p);
		
		assertTrue(scores.didTransmitTo(p));
	}
	
	@Test
	public void testDrawingsSentWhenPlayerJoinsAndActiveRound() {
		MockPlayer p = new MockPlayer("player3");
		
		game.start();
		game.playerDrew(new LineDrawingMessage(), player2);
		
		game.playerJoined(p);
		
		assertTrue("When a player joins a game and a round is active, drawings should be sent to the new player", round.wereDrawingsSentTo(p));
	}
	
	@Test
	public void testPlayerRemovedFromGameOnQuit() {
		game.playerQuit(player1);
		
		assertFalse(game.isParticipating(player1));
	}
	
	@Test
	public void testPlayerRemovedFromRoundWhenQuits() {
		game.start();
		game.playerQuit(player3);
		
		assertTrue(round.didRecieveQuitOf(player3));
	}
	
	@Test
	public void testRoundIsCancelledWhenInsufficientPlayers() {
		game.start();
		game.playerQuit(player3);
		game.playerQuit(player2);
		
		assertTrue(round.isCancelled());
	}
	
	@Test
	public void testScoresAreSentToRoomWhenRoundIsCancelledDueToInsufficientPlayers() {
		game.start();
		game.playerQuit(player3);
		
		scores.clearSentTo();
		game.playerQuit(player2);
		
		assertTrue(scores.didTransmitTo(room));
	}
	
	@Test
	public void testGameOverOnRoomWhenRoundIsCancelledDueToInsufficientPlayers() {
		game.start();
		game.playerQuit(player3);
		
		scores.clearSentTo();
		game.playerQuit(player2);
		
		assertTrue(room.didGameEnd());
	}
	
	@Test
	public void testScoresAreTracked() {
		game.start();
		game.award(player1, 10);
		
		assertEquals(10, scores.getPoints(player1));
	}
	
	@Test
	public void testPlayerIsNotifiedOfAward() {
		game.start();
		game.award(player1, 10);
		
		assertTrue(player1.didRecieveMessageOfType(AwardMessage.class));
	}
	
	@Test
	public void testMessagesAreSentToRoom() {
		game.sendMessage(new MockMessage());
		
		assertTrue(room.didRecieveMessageOfType(MockMessage.class));
	}
	
	@Test
	public void testMessagesAreSentToRoom2() {
		game.sendMessageToAllBut(player1, new MockMessage());
		
		assertTrue(room.didRecieveMessageOfType(MockMessage.class));
	}
	
	@Test
	public void testNewRoundStartsOnRoundComplete() {
		game.roundComplete();
		
		assertEquals(1, roundFactory.countOfRoundsCreated());
		assertTrue(round.didStart());
	}
	
	@Test
	public void testNewWordSelectedOnRoundComplete() {
		game.roundComplete();
		
		assertEquals(1, wordProvider.countOfWordsWorvided());
	}
	
	@Test
	public void testNewPlayerSelectedOnRoundComplete() {
		game.roundComplete();
		
		assertEquals(1, playerSelector.countOfPlayersSelected());
	}
	
	@Test
	public void testGameOverOnRoundCompleteAndNoRemainingPlayerTurns() {
		playerSelector.setNoMorePlayers();
		game.roundComplete();
		
		assertTrue(room.didGameEnd());
	}
	
	@Test
	public void testGuessesFromParticipatingPlayersSentToRound() {
		game.start();
		game.playerGuessed(new GuessMessage(), player1);
		
		assertTrue(round.didRecieveGuessFrom(player1));
	}
	
	@Test
	public void testGuessesFromNonParticipatingPlayersNotSentToRound() {
		MockPlayer p = new MockPlayer("some player");
		
		game.start();
		game.playerGuessed(new GuessMessage(), p);
		
		assertFalse(round.didRecieveGuessFrom(p));
	}
	
	@Test
	public void testDrawingsFromParticipatingPlayersSentToRound() {
		game.start();
		game.playerDrew(new LineDrawingMessage(), player1);
		
		assertTrue(round.didRecieveDrawingFrom(player1));
	}
	
}
