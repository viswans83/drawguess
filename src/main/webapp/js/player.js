define(function() {
	
	var Player = function(input) {
		var self = this
		
		this.input = input
		
		input.addEventListener('keydown', function(ev) {
			if (event.which == 13) {
				self.connection.send({guess: guess.value})
				input.value = ''
			}
		})
	}
	
	Player.prototype = {
		constructor: Player,
		
		setConnection: function(connection) {
			this.connection = connection
		},
		
		enableGuessing: function() {
			var guess = this.input
			
			guess.value = ''
			guess.disabled = false
			guessingEnabled = true
		},
		
		disableGuessing: function() {
			var guess = this.input
			
			guess.value = ''
			guess.disabled = true
			guessingEnabled = false
		}
	}
	
	return Player
})