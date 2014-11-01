define(['color'], function(Color) {
	
	var Image = function(img) {
		this.img = img
	}

	Image.prototype = {
		constructor : Image,

		getColor : function(p) {
			var c = this.img.data
			var pos = ((p.y - 1) * 300 + p.x) * 4

			return new Color(c[pos + 0], c[pos + 1], c[pos + 2])
		},

		setColor : function(p, color) {
			var c = this.img.data
			var pos = ((p.y - 1) * 300 + p.x) * 4

			c[pos + 0] = color.r
			c[pos + 1] = color.g
			c[pos + 2] = color.b
			c[pos + 3] = 255
		},
		
		displayOn: function(context) {
			context.putImageData(this.img, 0, 0)
		}
	}

	return Image
})