define(function() {
	
	var Connection = function(socket) {
		var self = this
		
		this.socket = socket
		
		socket.onmessage = function(ev) {
			self.listener.handle(JSON.parse(ev.data))
		}
		
		socket.onclose = function(ev) {
			self.listener.close()
		}
	}
	
	Connection.prototype = {
		constructor: Connection,
		
		setListener: function(listener) {
			this.listener = listener
		},
		
		send: function(msg) {
			this.socket.send(JSON.stringify(msg))
		}
	}
	
	return Connection
})