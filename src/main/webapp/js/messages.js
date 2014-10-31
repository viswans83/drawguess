define(function() {
	
	var newDiv = function (value) {
		var r = document.createElement('div')
		r.appendChild(document.createTextNode(value))
		return r
	}
	
	var Messages = function(container) {
		this.container = container
	}
	
	Messages.prototype = {
		constructor: Messages,
		
		add: function(msg) {
			var messages = this.container
			var fc = messages.firstChild
			
			messages.insertBefore(newDiv(msg), fc)
		}
	}
	
	return Messages
})