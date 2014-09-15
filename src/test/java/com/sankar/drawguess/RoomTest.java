package com.sankar.drawguess;

import org.junit.After;
import org.junit.Assert;
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
	
	@After
	public void after() {
		player1 = player2 = player3 = player4 = null;
		gameFactory = null;
		room = null;
	}
	
	@Test
	public void testReturnsCorrectName() {
		Assert.assertEquals("Room should return its name correctly", "a room", room.getName());
	}
	
	@Test
	public void testPlayerRecievesInsufficientPlayersMessage() {
		room.playerJoined(player1);
		
		Assert.assertTrue(
				"Player should recieve InsufficientPlayersMessage when " +
				"room doesnt have sufficient players to start a new game", 
				player1.didRecieveMessageOfType(InsufficientPlayersMessage.class));
	}
	
	@Test
	public void testExistingPlayerNotifiedOfWhenNewPlayerJoinsRoom() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		
		Assert.assertTrue(
				"Existing players in room should recieve a PlayerJoinedMessage " +
				"when a new player joins the room", 
				player1.didRecieveMessageOfType(PlayerJoinedMessage.class));
	}
	
	@Test
	public void testNewGameIsCreatedWhenThreePlayersJoinRoom() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		
		Assert.assertEquals("A new game should be created when sufficient players join the room", 1, gameFactory.countOfGamesCreated());
	}
	
	@Test
	public void testNewGameIsStartedWhenThreePlayersJoinRoom() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		
		Assert.assertTrue("A new game should be created when sufficient players join the room", gameFactory.getGame().didStart());
	}
	
	@Test
	public void testPlayerRecievesGameInProgressMessage() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		room.playerJoined(player4);
		
		Assert.assertTrue("Player should recieve GameInProgressMessage when room has an in-progress game", player4.didRecieveMessageOfType(GameInProgressMessage.class));
	}
	
	@Test
	public void testExistingPlayerNotifiedWhenNewPlayerQuitsRoom() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		
		room.playerQuit(player1);
		
		Assert.assertTrue(
				"Existing players in room should recieve a PlayerQuitMessage " +
				"when an existing player leaves the room", 
				player2.didRecieveMessageOfType(PlayerQuitMessage.class));
	}
	
	@Test
	public void testGameNotifiedWhenNewPlayerQuitsRoom() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		
		room.playerQuit(player1);
		
		Assert.assertTrue("The in-progress game should be notified when an existing player leaves the room", gameFactory.getGame().didQuit(player1));
	}
	
	@Test
	public void testGuessAreForwardedToInProgressGame() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		
		room.playerGuessed(new GuessMessage(), player1);
		
		Assert.assertTrue("Guesses in the room should be forwarded to the in-progress game", gameFactory.getGame().didGuess(player1));
	}
	
	@Test
	public void testDrawingsAreForwardedToInProgressGame() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		
		room.playerDrew(new LineDrawingMessage(), player1);
		
		Assert.assertTrue("Drawings in the room should be forwarded to the in-progress game", gameFactory.getGame().didDraw(player1));
	}
	
	@Test
	public void testMessagesToRoomAreRelayedToAllPlayers() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		
		room.sendMessage(new MockMessage());
		
		Assert.assertTrue("Messages sent to the room should be forwarded to all players", player1.didRecieveMessageOfType(MockMessage.class));
		Assert.assertTrue("Messages sent to the room should be forwarded to all players", player2.didRecieveMessageOfType(MockMessage.class));
	}
	
	@Test
	public void testMessagesToRoomAreRelayedToAppropriatePlayers() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		
		room.sendMessageToAllBut(player1, new MockMessage());
		
		Assert.assertFalse("Messages sent to the room should be forwarded to appropriate players", player1.didRecieveMessageOfType(MockMessage.class));
		Assert.assertTrue("Messages sent to the room should be forwarded to appropriate players", player2.didRecieveMessageOfType(MockMessage.class));
		Assert.assertTrue("Messages sent to the room should be forwarded to appropriate players", player3.didRecieveMessageOfType(MockMessage.class));
	}
	
	@Test
	public void testNewGameShouldStartWhenCurrentGameCompletes() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		
		room.gameOver();
		
		Assert.assertEquals("Room much create a new game when the current game completes and there are sufficient players", 2, gameFactory.countOfGamesCreated());
	}
	
	@Test
	public void testNewGameShouldNotStartWhenCurrentGameCompletes() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		
		room.playerQuit(player1);
		room.gameOver();
		
		Assert.assertEquals("Room must not create a new game when the current game completes and there are insufficient players", 1, gameFactory.countOfGamesCreated());
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
		
		Assert.assertTrue(
				"Existing players should recieve InsufficientPlayersMessage " + 
				"when the current game completes and there are insufficient players to begin " + 
				"a new game", 
				player2.didRecieveMessageOfType(InsufficientPlayersMessage.class));
		
		Assert.assertTrue(
				"Existing players should recieve InsufficientPlayersMessage " + 
				"when the current game completes and there are insufficient players to begin " + 
				"a new game", 
				player3.didRecieveMessageOfType(InsufficientPlayersMessage.class));
	}
	
	@Test
	public void testPlayersRecieveGameOverMessage() {
		room.playerJoined(player1);
		room.playerJoined(player2);
		room.playerJoined(player3);
		
		room.gameOver();
		
		Assert.assertTrue("Players should recieve GameOverMessage when game completes", player1.didRecieveMessageOfType(GameOverMessage.class));
		Assert.assertTrue("Players should recieve GameOverMessage when game completes", player2.didRecieveMessageOfType(GameOverMessage.class));
		Assert.assertTrue("Players should recieve GameOverMessage when game completes", player3.didRecieveMessageOfType(GameOverMessage.class));
	}
	
	@Test
	public void testReportsPlayerpresentCorrectly() {
		room.playerJoined(player1);
		
		Assert.assertTrue("Room should report players present correctly", room.isPresent(player1));
	}

}
