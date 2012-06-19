/**
 * @author Mark van der Tol
 */
var CanvasGraph = function ($canvas, $input, backgroundColor, pointColor, lineColor, axesColor) {
	var my = {};
	
	var points = [];
	var maxValueInPoints = 0;
	var circleRadius = 6;
	var clickCircleRadius = circleRadius + 2;
	var margin = 15;
	
	var width = $canvas.width();
	var height = $canvas.height();
	var canvasContext = $canvas[0].getContext("2d");
	
	
	function getXCoordinateForPoint(index) {
		return margin + (width - 2 * margin) / points.length * index;
	}
	
	function getYCoordinateForPoint(index) {
		return height - margin - ((height - 2 * margin) / maxValueInPoints * points[index].value);
	}
	
	function setmaxValueInPoints() {
		for (var i = 0; i < points.length; i++) {
			if (points[i].value > maxValueInPoints) {
				maxValueInPoints = points[i].value; 
			}
		}
	}
	
	function getPointOnCoordinate(mouseX, mouseY) {
		for (var i = 0; i < points.length; i++) {
			var pointX = getXCoordinateForPoint(i);
			var pointY = getYCoordinateForPoint(i);
			
			if (points[i].hidden)
				continue;
			
			canvasContext.beginPath();
			canvasContext.arc(pointX, pointY, clickCircleRadius, 0, Math.PI*2, true); 
			canvasContext.closePath();
			
			if (canvasContext.isPointInPath(mouseX, mouseY)) {
				return points[i];
			}
		}
		return null;
	}
	
	function draw() {	
		canvasContext.fillStyle = backgroundColor;
		canvasContext.fillRect(0, 0, width, height);
		
		drawLine();
		drawCircles();
		drawAxes();
		
		function drawAxes() {
			
			canvasContext.strokeStyle = axesColor;
			canvasContext.lineWidth = 1;
			
			canvasContext.moveTo(margin, height-margin);
			canvasContext.lineTo(width-margin, height-margin);
			
			canvasContext.moveTo(margin, height-margin);
			canvasContext.lineTo(margin, margin);
			
			canvasContext.stroke();
		}
				
		function drawLine() {
			canvasContext.strokeStyle = lineColor;
			canvasContext.lineWidth = 2;


			canvasContext.beginPath();
			var isFirst = true;
			for (var i = 0; i < points.length; i++) {
				var x = getXCoordinateForPoint(i);
				var y = getYCoordinateForPoint(i);
				
				if (points[i].hidden)
					continue;
				
				if (isFirst) {
					isFirst = false;
					canvasContext.moveTo(x, y);
				} else {
					canvasContext.lineTo(x, y);
				}
			}
			
			canvasContext.stroke();
		}
		
		function drawCircles() {
			canvasContext.fillStyle = pointColor;
			
			for (var i = 0; i < points.length; i++) {
				var x = getXCoordinateForPoint(i);
				var y = getYCoordinateForPoint(i);
				
				if (points[i].hidden)
					continue;
				
				canvasContext.beginPath();
				canvasContext.arc(x, y, circleRadius, 0, Math.PI*2, true); 
				canvasContext.closePath();
				canvasContext.fill();
			}
		}
	}
	
	
	$canvas.click(function (ev) {
		var x = ev.pageX - this.offsetLeft;
        var y = ev.pageY - this.offsetTop;
        var point = getPointOnCoordinate(x, y);
        
        if (point !== null) {
        	$input.val(point.id);
        }
		my.onclick(ev, point);
	});
	
	$canvas.mousemove(function (ev) {
		var x = ev.pageX - this.offsetLeft;
        var y = ev.pageY - this.offsetTop;
        var point = getPointOnCoordinate(x, y);
		
		my.onmousemove(ev, point);
	});
	
	
	my.onmousemove = function (evt, point) {};
	my.onclick = function (evt, point) {};
	
	my.setpoints = function (_points) {
		points = _points;
		setmaxValueInPoints();
		draw();
	};
	
	return my;
};

var CanvasGraphTableBinding = function (canvasGraph, $table, idColumnClass, valueColumnClass, visibleCheckboxClass) {
	var my = {};
	
	function getPointsFromTable() {
		var dataPoints = [];

		$table.find("tbody tr").each(function () {
			var $el = $(this);
			var dataPoint = {
					id: parseInt($.trim($el.find("." + idColumnClass).html())),
					value: parseInt($.trim($el.find("." + valueColumnClass).html())),
			};
			if (visibleCheckboxClass) {
				dataPoint.hidden = !$el.find("input." + visibleCheckboxClass).is(':checked');
			}
			dataPoints.push(dataPoint);
		});

		return dataPoints;
	}
	
	function registerCheckboxesListeners() {
		$table.find("input." + visibleCheckboxClass).click(function () {
			my.update();
		});
	}
	
	registerCheckboxesListeners();
	
	my.highlightRow = function(id, cssClass) {
		$table.find("tbody tr").each(function () {
			var $el = $(this);
			var rowId = parseInt($.trim($el.find("." + idColumnClass).html()));
			if (id == rowId) {
				$el.addClass(cssClass);
			} else {
				$el.removeClass(cssClass);
			}

		});
	};
	
	my.update = function () {
		canvasGraph.setpoints(getPointsFromTable());
	};
	
	return my;
};