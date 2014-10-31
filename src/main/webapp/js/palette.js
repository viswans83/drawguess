define(['color'], function(Color) {
	
	var newSpan = function () {
		var r = document.createElement('span')
		r.style.paddingLeft = '37.5px'
		return r
	}
	
	var rgb = function(r, g, b) {
		return new Color(r, g, b)
	}
	
	var colors = [
	    rgb(0, 31, 63),
		rgb(0, 116, 217),
		rgb(127, 219, 255),
		rgb(57, 204, 204),
		rgb(61, 153, 112),
		rgb(46, 204, 64),
		rgb(1, 255, 112),
		rgb(255, 220, 0),
		rgb(255, 133, 27),
		rgb(255, 65, 54),
		rgb(133, 20, 75),
		rgb(240, 18, 190),
		rgb(177, 13, 201),
		rgb(17, 17, 17),
		rgb(170, 170, 170),
		rgb(221, 221, 221)
	]
	
	var Palette = function(pal) {
		var self = this
		
		this.color = colors[13]
		
		colors.forEach(function(c) {
			var item = newSpan()
			
			item.style.backgroundColor = c.cssColor()
			item.data = c
			item.addEventListener('click', function(ev) {
				self.color = c
			})
			pal.appendChild(item)
		})
	}
	
	return Palette
})