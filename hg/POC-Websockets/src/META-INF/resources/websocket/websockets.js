/**
 * @author Mark van der Tol
 */
var liveSocket = (function () {
	var my = {};
	
	var LiveWebSocket = function (socketUrl, callback) {
		var innerMy = {};
		var connection = undefined;
		var url = socketUrl;
		
		innerMy.isSupported = function () {
			return "WebSocket" in window;
		};
		
		innerMy.getType = function () {
			return "WebSocket";
		};
		
		innerMy.open = function (socketUrl) {
			connection = new WebSocket(url);
			
			connection.onopen = function (evt) {
				callback.onOpen();
			};
			
			connection.onerror = function (evt) {
				connection.close();
				callback.onError(evt);
			};
			
			connection.onmessage = function (evt) {
				callback.onMessage(evt.data);
				callback.onObjectMessage(JSON.parse(evt.data));
			};
			
			connection.onclose = function (evt) {
				callback.onClose(evt);
			};
		};
		
		innerMy.sendMessage = function (message) {
			connection.send(message);
		};
		
		return innerMy;
	};
	
	var AjaxSocket = function (socketUrl, callback) {
		var innerMy = {};
		var onOpenSend = false;
		var url = socketUrl;
		
		innerMy.isSupported = function () {
			return true;
		};
		
		innerMy.getType = function () {
			return "Ajax";
		};
		
		innerMy.open = function () {
			$.ajax({
				url: url,
				type: "POST",
				beforeSend: function () {
					if (!onOpenSend) {
						callback.onOpen();
						onOpenSend = true;
					}
				},
				success: function (data) {
					callback.onMessage(JSON.stringify(data));
					callback.onObjectMessage(data);
					innerMy.open();
				},
				error: function (jqXHR, error) {
					callback.onError(error);
				}
			});
		};
		
		innerMy.sendMessage = function (message) {
			$.ajax({
				url: url,
				type: "POST",
				data: message
			});
		};
		
		return innerMy;
	};
	
	var LiveSocket = function (websocketUrl, ajaxUrl) {
		var innerMy = {};
		var connection;
			
		connection = LiveWebSocket(websocketUrl, innerMy);
		if (!connection.isSupported())
			connection = AjaxSocket(ajaxUrl, innerMy);
		
		innerMy.open = function () {
			connection.open();
		};
		
		innerMy.sendMessage = function(message) {
			connection.sendMessage(message);
		};
		
		innerMy.sendObjectMessage = function (obj) {
			innerMy.sendMessage(JSON.stringify(obj));
		};
		
		innerMy.getType = function () {
			return connection.getType();
		};
		
		innerMy.onMessage = function (message) {};
		
		innerMy.onObjectMessage = function (obj) {};
		innerMy.onError = function () {};
		innerMy.onOpen = function () {};
		innerMy.onClose = function () {};
		
		return innerMy;
	};
	
	my.open = function (websocketUrl, ajaxUrl) {
		return LiveSocket(websocketUrl, ajaxUrl);
	};
	
	return my;
})();
