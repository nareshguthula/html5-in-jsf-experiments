
var message = (function () {
	var prefix = "message_";
	var targetDiv = null;
	var maxId = 50;
	
	function fetch(id, callback) {
		if (id < 1 || id > maxId)
			return;
		
		$.ajax("/POC-SessionStorage/message.xhtml?render=messageTarget&message=" + id, {
			success: function (data) {
				if (data == "null")
					return;				
				
				localStorage.setItem(prefix + id, data);
				
				if (callback) {
					callback(data);
				}
			}
		});
	}
	
	function prefetch(currentId) {
		
		for (var i = currentId - 2; i <= currentId + 2; i++) {
			if (i == currentId)
				continue;
			
			var data = localStorage.getItem(prefix + i);
			
			if (data == null) {
				fetch (i);
			}
		}
	}
	
	function presentResult(data) {
		$(targetDiv).replaceWith(data);
	}
	
	return {
		showMessage: function (id) {
			var data = localStorage.getItem(prefix + id);
			
			if (data == null) {
				fetch(id, function (data) {
					presentResult(data);
					prefetch(id);
				});
			} else {
				presentResult(data);
				prefetch(id);
			}
		},
		setTargetDiv: function (target) {
			targetDiv = target;
		},
		setMaxId: function (max) {
			maxId = max;
		}
	};
})();
