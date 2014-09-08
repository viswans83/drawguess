function onload() {
	var room = prompt('Enter room: ', 'default')
	var name = prompt('Enter name: ', 'default')
	
	var guess = document.getElementById('guess')
	var messages = document.getElementById('messages')
	
	var can = document.getElementById('drawing')
	var ctx = can.getContext('2d')
	
	var wsurl = document.getElementById('wsurl')
	var sock = new WebSocket('ws://' + wsurl.host + wsurl.pathname + '/' + room + '/' + name)
	
	var drawingEnabled = false;
	var guessingEnabled = false;
	
	var fill_color = {r: 0, g: 0, b: 0}
	var setupColors = function() {
		var pal = document.getElementById('colors')
		
		var newSpan = function () {
			var r = document.createElement('span')
			r.style.paddingLeft = '37.5px'
			return r
		}
		
		var rgb = function(r, g, b) {
			return {
				r: r,
				g: g,
				b: b
			}
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
		
		colors.forEach(function(c) {
			var item = newSpan()
			item.style.backgroundColor = 'rgb(' + c.r + ',' + c.g + ',' + c.b + ')'
			item.data = c
			item.addEventListener('click', function(ev) {
				fill_color = c
			})
			pal.appendChild(item)
		})
		
	}
	
	setupColors()
	
	sock.onclose = function(ev) {
		clearCanvas()
		addMessage('** You have been disconnected from this game room **')
	}
	
	sock.onmessage = function(ev) {
		var msg = JSON.parse(ev.data)
		var str
		
		if (msg.award) {
			addMessage('You scored ' + msg.award + ' Points')
		}
		else if (msg.floodFill) {
			floodFill(msg.floodFill, msg.color)
		}
		else if (msg.gameInProgress) {
			disableGuessing();
			disableDrawing();
			addMessage('Game is in progress, spectating until next round..')
		}
		else if (msg.guess) {
			addMessage(msg.who + ' guessed: ' + msg.guess)
		}
		else if (msg.insufficientPlayers) {
			disableGuessing();
			disableDrawing();
			addMessage('Waiting for additional players..')
		}
		else if (msg.lineDrawing) {
			draw_points(msg.lineDrawing)
		}
		else if (msg.newGame) {
			addMessage('*** New game Starting ***')
		}
		else if (msg.newRound) {
			addMessage('New round Starting')
			
			clearCanvas()
			
			disableDrawing()
			disableGuessing()
		}
		else if (msg.newWord) {
			clearCanvas()
			addMessage('Your turn to draw! Your word: ' + msg.newWord)
			enableDrawing()
			disableGuessing()
		}
		else if (msg.playerJoined) {
			addMessage('Player joined: ' + msg.playerJoined)
		}
		else if (msg.playerQuit) {
			addMessage('Player quit: ' + msg.playerQuit)
		}
		else if (msg.roundComplete) {
			addMessage('Round complete, the word was: ' + msg.originalWord)
		}
		else if (msg.scores) {
			str = 'Current Scores: '
			
			msg.scores.forEach(function(v) {
				str = str + v.name + '(' + v.score + '), '
			})
			str = str.slice(0, str.length - (msg.scores.length == 0 ? 0 : 2))
			
			addMessage(str)
		}
		else if (msg.startGuessing) {
			enableGuessing();
			addMessage(msg.who + ' is now drawing, start guessing!')
		}
		else if (msg.ticks) {
			addMessage(msg.ticks + ' seconds left in this round')
		}
		else if (msg.wordGuessed) {
			addMessage(msg.who + ' has guessed correctly')
		}
		else {
			console.log('Unknown message: ' + msg)
		}
	}
	
	function enableGuessing() {
		guess.value = ''
		guess.disabled = false
		guessingEnabled = true
	}
	
	function disableGuessing() {
		guess.value = ''
		guess.disabled = true
		guessingEnabled = false
	}
	
	function enableDrawing() {
		can.addEventListener('mousemove', moveHandler, false)
		can.addEventListener('click', clickHandler, false)
		drawingEnabled = true
	}
	
	function disableDrawing() {
		can.removeEventListener('mousemove', moveHandler, false)
		can.removeEventListener('click', clickHandler, false)
		drawingEnabled = false
	}
	
	var addMessage = function (value) {
		var fc = messages.firstChild
		messages.insertBefore(newDiv(value), fc)
	}
	
	var newDiv = function (value) {
		var r = document.createElement('div')
		r.appendChild(document.createTextNode(value))
		return r
	}

	var line_params = function (x1, y1, x2, y2) {
	    var m = (y2 - y1) / (x2 - x1)
	    var c = y1 - (m * x1)

	    return {
	        a: m,
	        b: -1,
	        c: c,
	        x: (x1 == x2) ? x1 : undefined,
	        y: (y1 == y2) ? y1 : undefined
	    }
	}

	var point_dist = function (point, line) {
	    var x = point.x
	    var y = point.y

	    var a = line.a
	    var b = line.b
	    var c = line.c

	    var num = a * x + b * y + c
	    var den = Math.sqrt(a * a + b * b)

	    if (line.x) dist = (point.x - line.x)
	    else if (line.y) dist = (point.y - line.y)
	    else dist = num / den

	    return (dist < 0) ? -dist : dist
	}

	var max_dist_point = function (points) {
	    var n = points.length
	    var p1 = points[0]
	    var p2 = points[n - 1]
	    var line = line_params(p1.x, p1.y, p2.x, p2.y)

	    var indx = undefined
	    var dist = 0
	    var p = undefined

	    for (var i = 1; i < n - 1; i++) {
	        var d = point_dist(points[i], line)
	        if (d > dist) {
	            indx = i
	            dist = d
	            p = points[i]
	        }
	    }

	    return {
	        indx: indx,
	        dist: dist
	    }
	}

	var reduce_points = function (points, rpoints) {
	    var n = points.length
	    var mid = undefined

	    if (n > 2) {
	        mid = max_dist_point(points)
	        if (mid.dist > 1) {
	            reduce_points(points.slice(0, mid.indx + 1), rpoints)
	            reduce_points(points.slice(mid.indx, n), rpoints)
	            return rpoints
	        } else {
	            rpoints.push(points[n - 1])
	            return rpoints
	        }
	    } else {
	        rpoints.push(points[1])
	    }

	    return rpoints
	}

	var minimize_points = function (points) {
	    var result = []

	    result.push(points[0])
	    reduce_points(points, result)

	    return result
	}
	
	var draw_points = function(points) {
		ctx.beginPath()
        for (var i = 0; i < points.length; i++) {
            if (i == 0) 
                ctx.moveTo(points[i].x, points[i].y)
            else
                ctx.lineTo(points[i].x, points[i].y)
        }
        ctx.stroke()
	}
	
	var floodFill = function (p, targetColor) {
		var getColor = function (img, p) {
			var pos = ((p.y - 1) * 300 + p.x) * 4
		    var c = img.data
		    return {
		        r: c[pos + 0],
		        g: c[pos + 1],
		        b: c[pos + 2],
		        a: c[pos + 3]
		    }
		}

		var setColor = function (img, p, color) {
			var pos = ((p.y - 1) * 300 + p.x) * 4
		    var c = img.data
		    c[pos + 0] = color.r
		    c[pos + 1] = color.g
		    c[pos + 2] = color.b
		    c[pos + 3] = 255
		}
		
		var colorEq = function (c1, c2) {
		    return c1.r == c2.r && c1.g == c2.g && c1.b == c2.b && c1.a == c2.a
		}
		
		var img = ctx.getImageData(0, 0, 300, 300)
		
	    var origColor = getColor(img, p)
	    var pts = []

	    var left, right, leftc, rightc, coloru, colord

	    pts.push(p)
	    while (pts.length > 0) {
	        p = pts.pop()
	        
	        if (colorEq(targetColor, getColor(img, p))) continue;
	        
	        left = {
	            x: p.x,
	            y: p.y
	        }
	        right = {
	            x: p.x,
	            y: p.y
	        }

	        while (left.x > 0) {
	            left.x = left.x - 1
	            leftc = getColor(img, left)
	            if (!colorEq(leftc, origColor)) break
	        }

	        left.x = left.x + 1

	        while (right.x < 300) {
	            right.x = right.x + 1
	            rightc = getColor(img, right)
	            if (!colorEq(rightc, origColor)) break
	        }

	        right.x = right.x - 1	        

	        for (; left.x <= right.x; left.x = left.x + 1) {
	            setColor(img, left, targetColor)
	            
	            if (left.y - 1 >= 0) {
		            coloru = getColor(img, {
			            x: left.x,
			            y: left.y - 1
			        })
			        if (colorEq(origColor, coloru)) {
			            pts.push({
			                x: left.x,
			                y: left.y - 1
			            })
			        }
	            }
	
	            if (left.y + 1 < 300) {
		            colord = getColor(img, {
			            x: left.x,
			            y: left.y + 1
			        })
			        if (colorEq(origColor, colord)) {
			            pts.push({
			                x: left.x,
			                y: left.y + 1
			            })
			        }
	            }
	        }
	    }
	    
	    ctx.putImageData(img, 0, 0)
	}
	
	var points_acc = []
	var collecting = false

	var old_x, old_y

	var last_mx, last_my
	
	var moveHandler = function (ev) {
	    var new_x = ev.pageX - can.offsetLeft
	    var new_y = ev.pageY - can.offsetTop
	    
	    last_mx = new_x
	    last_my = new_y

	    if (collecting) {
	        ctx.beginPath()
	        ctx.moveTo(old_x, old_y)
	        ctx.lineTo(new_x, new_y)
	        ctx.stroke()

	        points_acc.push({
	            x: new_x,
	            y: new_y
	        })
	        
	        old_x = new_x
	        old_y = new_y
	    }
	}

	var clickHandler = function (ev) {
	    var new_x = ev.pageX - can.offsetLeft
	    var new_y = ev.pageY - can.offsetTop

	    // Start collecting
	    if (!collecting) {
	        
	        old_x = new_x
	        old_y = new_y
	        
	        points_acc = []
	        points_acc.push({
	            x: new_x,
	            y: new_y
	        })
	    }

	    collecting = !collecting

	    // Stop collecting
	    if (!collecting) {
	        points_acc.push({
	            x: new_x,
	            y: new_y
	        })
	        
	        var r = minimize_points(points_acc)
	        sock.send(JSON.stringify({lineDrawing: r}))
	    }
	}
	
	var clearCanvas = function() {
		ctx.clearRect(0, 0, 300, 300)
		ctx.strokeRect(0, 0, 300, 300)
	}

	ctx.lineWidth = 1
	ctx.lineCap = 'round'
	ctx.strokeStyle = 'black'		
	
	clearCanvas()
	disableDrawing()
	disableGuessing()
	
	guess.addEventListener('keydown', function(ev) {
		if (event.which == 13) {
			sock.send(JSON.stringify({guess: guess.value}))
			guess.value = ''
		}
	})
	
	document.body.addEventListener('keydown', function(ev) {
		if (drawingEnabled && event.which == 70 && last_mx && last_my) {
			
			floodFill({
				x: last_mx,
				y: last_my
			}, fill_color)
			
			sock.send(JSON.stringify({
				floodFill: {
					x: last_mx,
					y: last_my
				},
				color: fill_color
			}))
		}
	})
}