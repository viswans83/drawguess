define(function() {
	
	var Color = function(r, g, b) {
		this.r = r
		this.g = g
		this.b = b
	}

	Color.prototype = {
		constructor : Color,

		equals : function(c2) {
			var c1 = this

			return c1.r == c2.r && c1.g == c2.g && c1.b == c2.b
		},
		
		cssColor: function() {
			var c = this
			return 'rgb(' + c.r + ',' + c.g + ',' + c.b + ')'
		}
	}

	return Color
})