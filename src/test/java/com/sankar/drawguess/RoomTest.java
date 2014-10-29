package com.sankar.drawguess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.sankar.drawguess.mocks.MockGameFactory;
import com.sankar.drawguess.mocks.MockMessage;
import com.sankar.drawguess.mocks.MockPlayer;
import com.sankar.drawguess.msg.GameInProgressMessage;
import com.sankar.drawguess.msg.GameOverMessage;
import com.sankar.drawguess.msg.GuessMessage;
import com.sankar.drawguess.msg.InsufficientPlayersMessage;
import com.sankar.drawguess.msg.LineDrawingMessage;
import com.sankar.drawguess.msg.PlayerJoinedMessage;
import com.sankar.drawguess.msg.PlayerQuitMessage;

public class RoomTest {
	
	private MockPlayer player1;
	private MockPlayer player2;
	private MockPlayer player3;
	private MockPlayer player4;
	private MockGameFactory gameFactory;
	private Room room;
	
	@Before
	public void before() {
		player1 = new MockPlayer("player1");
		player2 = new MockPlayer("player2");
		player3 = new MockPlayer("player3");
		player4 = new MockPlayer("player4");
		gameFactory = new MockGameFactory();
		
		room = new Room("a room", gameFactory);
	}
	
	@Test
	public void testReturnsCorrectName() {
		assertEquals("a room", room.getName());
	}
	
	@Test
	public void testPlayerRecievesInsufficientPlayersMessage() {
		room.playerJoined(player1);
		
		assertTrue(player1.didRecieveMessageOfType(InsufficientPlayersMessage.class));
	}
	
	@Test
	public void testExistingPlayerNotifiedOfWhenNewPlayerJoinsRoom() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		
		assertTrue(player1.didRecieveMessageOfType(PlayerJoinedMessage.class));
	}
	
	@Test
	public void testNewGameIsCreatedWhenThreePlayersJoinRoom() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		
		assertEquals(1, gameFactory.countOfGamesCreated());
	}
	
	@Test
	public void testNewGameIsStartedWhenThreePlayersJoinRoom() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		
		assertTrue(gameFactory.getGame().didStart());
	}
	
	@Test
	public void testPlayerRecievesGameInProgressMessage() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		room.playerJoined(player4);
		
		assertTrue(player4.didRecieveMessageOfType(GameInProgressMessage.class));
	}
	
	@Test
	public void testExistingPlayerNotifiedWhenNewPlayerQuitsRoom() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		
		room.playerQuit(player1);
		
		assertTrue(player2.didRecieveMessageOfType(PlayerQuitMessage.class));
	}
	
	@Test
	public void testGameNotifiedWhenNewPlayerQuitsRoom() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		
		room.playerQuit(player1);
		
		assertTrue(gameFactory.getGame().didQuit(player1));
	}
	
	@Test
	public void testGuessAreForwardedToInProgressGame() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		
		room.playerGuessed(new GuessMessage(), player1);
		
		assertTrue(gameFactory.getGame().didGuess(player1));
	}
	
	@Test
	public void testDrawingsAreForwardedToInProgressGame() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		
		room.playerDrew(new LineDrawingMessage(), player1);
		
		assertTrue(gameFactory.getGame().didDraw(player1));
	}
	
	@Test
	public void testMessagesToRoomAreRelayedToAllPlayers() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		
		room.sendMessage(new MockMessage());
		
		assertTrue(player1.didRecieveMessageOfType(MockMessage.class));
		assertTrue(player2.didRecieveMessageOfType(MockMessage.class));
	}
	
	@Test
	public void testMessagesToRoomAreRelayedToAppropriatePlayers() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		
		room.sendMessageToAllBut(player1, new MockMessage());
		
		assertFalse(player1.didRecieveMessageOfType(MockMessage.class));
		assertTrue(player2.didRecieveMessageOfType(MockMessage.class));
		assertTrue(player3.didRecieveMessageOfType(MockMessage.class));
	}
	
	@Test
	public void testNewGameShouldStartWhenCurrentGameCompletes() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		
		room.gameOver();
		
		assertEquals(2, gameFactory.countOfGamesCreated());
	}
	
	@Test
	public void testNewGameShouldNotStartWhenCurrentGameCompletes() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		
		room.playerQuit(player1);
		room.gameOver();
		
		assertEquals(1, gameFactory.countOfGamesCreated());
	}
	
	@Test
	public void testExistingPlayersShouldRecieveInsufficientPlayersMessageWhenCurrentGameCompletes() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		
		room.playerQuit(player1);
		
		player2.clearMessages();
		player3.clearMessages();
		
		room.gameOver();
		
		assertTrue(player2.didRecieveMessageOfType(InsufficientPlayersMessage.class));
		
		assertTrue(player3.didRecieveMessageOfType(InsufficientPlayersMessage.class));
	}
	
	@Test
	public void testPlayersRecieveGameOverMessage() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		
		room.gameOver();
		
		assertTrue(player1.didRecieveMessageOfType(GameOverMessage.class));
		assertTrue(player2.didRecieveMessageOfType(GameOverMessage.class));
		assertTrue(player3.didRecieveMessageOfType(GameOverMessage.class));
	}
	
	@Test
	public void testReportsPlayerPresentCorrectly() {
		room.playerJoined(player1);
		
		assertTrue(room.isPresent(player1));
	}

}
