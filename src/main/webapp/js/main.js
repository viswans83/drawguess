require(['vendor/domReady', 'palette', 'messages', 'player', 'connection', 'listener', 'drawing'],

function(domReady, Palette, Messages, Player, Connection, Listener, Drawing) {

	domReady(function() {
		var room = prompt('Enter room: ', 'default')
		var name = prompt('Enter name: ', 'default')

		var wsurl = document.getElementById('wsurl')
		var sock = new WebSocket('ws://' + wsurl.host + wsurl.pathname + '/' + room + '/' + name)

		// -------- Create Components --------- //

		var connection = new Connection(sock)

		var player = new Player(document.getElementById('guess'))

		var palette = new Palette(document.getElementById('colors'))

		var messages = new Messages(document.getElementById('messages'))

		var drawing = new Drawing(document.getElementById('drawing'), connection, palette)

		var listener = new Listener(player, drawing, messages)

		// -------- Connect Components --------- //

		connection.setListener(listener)

		player.setConnection(connection)
		
		drawing.clear()
		
		// Press 'd' to enable drawing in order to debug
		document.body.addEventListener('keydown', function(ev) {
			if (event.which == 68) {
				drawing.enableDrawing()
			}
		})
	})

})