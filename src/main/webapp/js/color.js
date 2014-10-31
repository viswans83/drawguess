define(function() {
	
	var Color = function(r, g, b, a) {
		this.r = r
		this.g = g
		this.b = b
		this.a = a || 255
	}

	Color.prototype = {
		constructor : Color,

		equals : function(c2) {
			var c1 = this

			return c1.r == c2.r && c1.g == c2.g && c1.b == c2.b && c1.a == c2.a
		},
		
		cssColor: function() {
			var c = this
			return 'rgb(' + c.r + ',' + c.g + ',' + c.b + ')'
		}
	}

	return Color
})