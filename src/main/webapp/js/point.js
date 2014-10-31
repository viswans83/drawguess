define(function() {
	
	var Point = function(x, y) {
		this.x = x
		this.y = y
	}

	Point.prototype = {
		constructor : Point,

		left : function() {
			return new Point(this.x - 1, this.y)
		},

		right : function() {
			return new Point(this.x + 1, this.y)
		},

		up : function() {
			return new Point(this.x, this.y - 1)
		},

		down : function() {
			return new Point(this.x, this.y + 1)
		},

		colorIn : function(img) {
			return img.getColor(this)
		},
		
		toString: function() {
			return '(' + this.x + ', ' + this.y + ')'
		}
	}

	return Point
})