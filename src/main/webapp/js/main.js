require(['vendor/domReady', 'palette', 'messages', 'player', 'connection', 'listener', 'drawing'],

function(domReady, Palette, Messages, Player, Connection, Listener, Drawing) {
	
	/** Credit: James Padolsey
	  * http://james.padolsey.com/javascript/getting-a-fully-qualified-url/
	***/
	function absoluteURL(proto, relpath) {
		var root = window.location.protocol + '://' + window.location.host + '/'
		
		var url
	    var img = document.createElement('img')
	    img.src = ''
	    url = img.src
	    
		var relstart = root.length - 2
		var rellength = url.length - root.length + 2
	    
		return proto + '://' + window.location.host + url.substr(relstart, rellength) + relpath;
	}

	domReady(function() {
		var room = prompt('Enter room: ', 'default')
		var name = prompt('Enter name: ', 'default')
		
		var sock = new WebSocket(absoluteURL('ws', 'ws/' + room + '/' + name))

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