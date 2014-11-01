define(['point'], function(Point) {
	
	var floodFill = function(img, p, targetColor) {
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
	}
	
	return floodFill
})