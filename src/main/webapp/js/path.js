define(function() {
	
	var lineParams = function (x1, y1, x2, y2) {
		// Line equation: ax + by + c = 0, with slope m
		// x and y are special cases where the equation is given by x = const or y = const
		
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
	
	var pointDistance = function (point, line) {
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
	
	var maxDistancePoint = function (points) {
	    var n = points.length
	    var p1 = points[0]
	    var p2 = points[n - 1]
	    var line = lineParams(p1.x, p1.y, p2.x, p2.y)

	    var indx = undefined
	    var dist = 0
	    var p = undefined

	    for (var i = 1; i < n - 1; i++) {
	        var d = pointDistance(points[i], line)
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
	
	var reducePoints = function (points, rpoints) {
	    var n = points.length
	    var mid = undefined

	    if (n > 2) {
	        mid = maxDistancePoint(points)
	        if (mid.dist > 1) {
	        	reducePoints(points.slice(0, mid.indx + 1), rpoints)
	            reducePoints(points.slice(mid.indx, n), rpoints)
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
	
	var Path = function() {
		this.points = arguments[0] || []
	}

	Path.prototype = {
		constructor: Path,
		
		append: function(point) {
			this.points.push(point)
		},
		
		minimized: function() {
			var result = []

		    result.push(this.points[0])
		    reducePoints(this.points, result)

		    return new Path(result)
		}
		
	}
	
	return Path
})