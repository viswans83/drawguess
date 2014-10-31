define(function() {
	
	var Listener = function(player, drawing, messages) {
		this.player = player
		this.drawing = drawing
		this.messages = messages
	}
	
	Listener.prototype = {
		constructor: Listener,
		
		close: function(ev) {
			this.drawing.clear()
			this.messages.add('** You have been disconnected from this game room **')
		},
		
		handle: function(msg) {
			var messages = this.messages
			var drawing = this.drawing
			var player = this.player

			if (msg.award) {
				messages.add('You scored ' + msg.award + ' Points')
			}
			
			else if (msg.floodFill) {
				drawing.floodFill(msg.floodFill, msg.color)
			}
			
			else if (msg.gameInProgress) {
				player.disableGuessing();
				drawing.disableDrawing();
				messages.add('Game is in progress, spectating until next round..')
			}
			
			else if (msg.gameOver) {
				messages.add('*** Game over ***')
			}
			
			else if (msg.guess) {
				messages.add(msg.who + ' guessed: ' + msg.guess)
			}
			
			else if (msg.insufficientPlayers) {
				player.disableGuessing();
				drawing.disableDrawing();
				messages.add('Waiting for additional players..')
			}
			
			else if (msg.lineDrawing) {
				drawing.draw(msg.lineDrawing)
			}
			
			else if (msg.newGame) {
				messages.add('*** New game Starting ***')
			}
			
			else if (msg.newRound) {
				messages.add('New round Starting')
				drawing.clear()
			}
			
			else if (msg.newWord) {
				drawing.clear()
				messages.add('Your turn to draw! Your word: ' + msg.newWord)
				drawing.enableDrawing()
				player.disableGuessing()
			}
			
			else if (msg.playerJoined) {
				messages.add('Player joined: ' + msg.playerJoined)
			}
			
			else if (msg.playerQuit) {
				messages.add('Player quit: ' + msg.playerQuit)
			}
			
			else if (msg.roundComplete) {
				messages.add('Round complete, the word was: ' + msg.originalWord)
				drawing.disableDrawing()
				player.disableGuessing()
			}
			
			else if (msg.roundCancelled) {
				messages.add('This round was cancelled since there is nobody to guess. The word in this round was: ' + msg.originalWord)
				drawing.disableDrawing()
				player.disableGuessing()
			}
			
			else if (msg.scores) {
				messages.add((function() {
					var str = 'Current Scores: '

					msg.scores.forEach(function(v) {
						str = str + v.name + '(' + v.score + '), '
					})
					str = str.slice(0, str.length - (msg.scores.length == 0 ? 0 : 2))
					
					return str
				})())
			}
			
			else if (msg.startGuessing) {
				player.enableGuessing();
				messages.add(msg.who + ' is now drawing, start guessing!')
			}
			
			else if (!isNaN(msg.ticks)) {
				if (msg.ticks > 0)
					messages.add(msg.ticks + ' seconds left in this round')
				else
					messages.add('Time up!')
			}
			
			else if (msg.wordGuessed) {
				messages.add(msg.who + ' has guessed correctly')
			}
			
			else {
				console.log('Unknown message: ' + msg)
			}
		}
	}
	
	return Listener
})