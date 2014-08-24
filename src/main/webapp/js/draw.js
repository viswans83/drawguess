function onload() {
	var room = prompt('Enter room: ', 'default')
	var name = prompt('Enter name: ', 'default')
	
	var guess = document.getElementById('guess')
	var messages = document.getElementById('messages')
	
	var can = document.getElementById('drawing')
	var ctx = can.getContext('2d')
	
	var wsurl = document.getElementById('wsurl')
	var sock = new WebSocket('ws://' + wsurl.host + wsurl.pathname + '/' + room + '/' + name)
	
	var timeRemaining = 60;
	
	sock.onclose = function(ev) {
		ctx.clearRect(0, 0, 300, 300)
		ctx.strokeRect(0, 0, 300, 300)
		addMessage('** You have been disconnected from this game room **')
	}
	
	sock.onmessage = function(ev) {
		var msg = JSON.parse(ev.data)
		var str
		
		if (msg.award) {
			addMessage('You scored ' + msg.award + ' Points')
		}
		else if (msg.drawing) {
			draw_points(msg.drawing)
		}
		else if (msg.emptyRoom) {
			disableGuessing();
			disableDrawing();
			addMessage('Room is empty, waiting for players to join..')
		}
		else if (msg.gameInProgress) {
			disableGuessing();
			disableDrawing();
			addMessage('Game is in progress, spectating until next round..')
		}
		else if (msg.guess) {
			addMessage(msg.who + ' guessed: ' + msg.guess)
		}
		else if (msg.newGame) {
			timeRemaining = 60
			addMessage('New Game Starting, you have 60 seconds')
			
			ctx.clearRect(0, 0, 300, 300)
			ctx.strokeRect(0, 0, 300, 300)
			
			disableDrawing()
			disableGuessing()
		}
		else if (msg.newWord) {
			ctx.clearRect(0, 0, 300, 300)
			ctx.strokeRect(0, 0, 300, 300)
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
		else if (msg.players) {
			str = 'Players in room: '
			
			msg.players.forEach(function(v) {
				str = str + v.name + '(' + v.score + '), '
			})
			str = str.slice(0, str.length - (msg.players.length == 0 ? 0 : 2))
			
			addMessage(str)
		}
		else if (msg.startGuessing) {
			enableGuessing();
			addMessage(msg.who + ' is now drawing, start guessing!')
		}
		else if (msg.ticks) {
			timeRemaining = timeRemaining - msg.ticks
			
			if (timeRemaining > 0)
				addMessage(timeRemaining + ' seconds left in this round')
			else
				addMessage('Time up!')
		}
		else if (msg.wordGuessed) {
			addMessage(msg.who + ' has guessed correctly')
		}
	}
	
	function enableGuessing() {
		guess.value = ''
		guess.disabled = false
	}
	
	function disableGuessing() {
		guess.value = ''
		guess.disabled = true
	}
	
	function enableDrawing() {
		can.addEventListener('mousemove', moveHandler, false)
		can.addEventListener('click', clickHandler, false)
	}
	
	function disableDrawing() {
		can.removeEventListener('mousemove', moveHandler, false)
		can.removeEventListener('click', clickHandler, false)
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

	var points_acc = []
	var collecting = false

	var old_x, old_y

	var moveHandler = function (ev) {
	    var new_x = ev.pageX - can.offsetLeft
	    var new_y = ev.pageY - can.offsetTop

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
	        sock.send(JSON.stringify({drawing: r}))
	    }
	}

	ctx.strokeStyle = 'black'

	ctx.clearRect(0, 0, 300, 300)
	ctx.strokeRect(0, 0, 300, 300)	
	
	disableDrawing()
	disableGuessing()
	
	guess.addEventListener('keydown', function(ev) {
		if(event.which == 13) {
			sock.send(JSON.stringify({guess: guess.value}))
			guess.value = ''
		}
	})
}