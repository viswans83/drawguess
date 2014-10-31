define(['point', 'path', 'image'], function(Point, Path, Image) {
	
	var Drawing = function(canvas, connection, palette) {
		var self = this
		
		var context
		var old_x, old_y, new_x, new_y
		var currentPath
		
		this.canvas = canvas
		this.drawingEnabled = false
		
		context = this.context = canvas.getContext('2d')
		context.lineWidth = 1
		context.lineCap = 'round'
		context.strokeStyle = 'black'
		
		this.clickHandler = function(ev) {
			old_x = ev.pageX - canvas.offsetLeft
			old_y = ev.pageY - canvas.offsetTop
			
			if (!currentPath) {
				currentPath = new Path()
				currentPath.append(new Point(old_x, old_y))
			}
			
			else {
				currentPath.append(new Point(old_x, old_y))
				connection.send({lineDrawing: currentPath.minimized().points})
				currentPath = undefined
			}
		}
		
		this.moveHandler = function(ev) {
			new_x = ev.pageX - canvas.offsetLeft
		    new_y = ev.pageY - canvas.offsetTop
		    
		    if (currentPath) {
		    	context.beginPath()
		        context.moveTo(old_x, old_y)
		        context.lineTo(new_x, new_y)
		        context.stroke()

		        currentPath.append(new Point(new_x, new_y))
		        
		        old_x = new_x
		        old_y = new_y
		    }
		}
		
		document.body.addEventListener('keydown', function(ev) {
			if (self.drawingEnabled && event.which == 70 && new_x && new_y) {
				
				self.floodFill({
					x: new_x,
					y: new_y
				}, palette.color)
				
				connection.send({
					floodFill: {
						x: new_x,
						y: new_y
					},
					color: palette.color
				})
			}
		})
		
	}

	Drawing.prototype = {
		constructor: Drawing,
		
		enableDrawing: function() {
			var can = this.canvas
			
			can.addEventListener('mousemove', this.moveHandler, false)
			can.addEventListener('click', this.clickHandler, false)
			
			this.drawingEnabled = true
		},
		
		disableDrawing: function() {
			var can = this.canvas
			
			can.removeEventListener('mousemove', this.moveHandler, false)
			can.removeEventListener('click', this.clickHandler, false)
			
			this.drawingEnabled = false
		},
		
		clear: function() {
			var ctx = this.context
			
			ctx.clearRect(0, 0, 300, 300)
			ctx.strokeRect(0, 0, 300, 300)
		},
		
		floodFill: function(p, targetColor) {
			var img = new Image(this.context.getImageData(0, 0, 300, 300))
			var pt = new Point(p.x, p.y)
		    var origColor = img.getColor(pt)
		    
		    var points = []
		    var left, right, leftc, rightc, coloru, colord

		    points.push(pt)
		    while (points.length > 0) {
		        pt = points.pop()
		        
		        if (targetColor.equals(img.getColor(pt))) continue;
		        
		        left = new Point(pt.x, pt.y)
		        right = new Point(pt.x, pt.y)

		        while (left.x > 0) {
		            left = left.left()
		            leftc = img.getColor(left)
		            if (!leftc.equals(origColor)) break
		        }

		        left = left.right()

		        while (right.x < 299) {
		            right = right.right()
		            rightc = img.getColor(right)
		            if (!rightc.equals(origColor)) break
		        }

		        right = right.left()	        

		        for (pt = left; pt.x <= right.x; pt = pt.right()) {
		            img.setColor(pt, targetColor)
		            
		            if (pt.y - 1 >= 0) {
			            coloru = img.getColor(pt.up())
				        if (origColor.equals(coloru)) {
				            points.push(pt.up())
				        }
		            }
		
		            if (pt.y + 1 < 300) {
			            colord = img.getColor(pt.down())
				        if (origColor.equals(colord)) {
				            points.push(pt.down())
				        }
		            }
		        }
		    }
		    
			img.displayOn(this.context)
		},
		
		draw: function(points) {
			var ctx = this.context
			
			ctx.beginPath()
	        for (var i = 0; i < points.length; i++) {
	            if (i == 0) 
	                ctx.moveTo(points[i].x, points[i].y)
	            else
	                ctx.lineTo(points[i].x, points[i].y)
	        }
	        ctx.stroke()
		}
		
	}
	
	return Drawing
})