/**
 * @author Mark van der Tol
 */
var directUpdate = (function () {
	var socket = undefined;
	var formFields = [];
	var $updatingRepeat = undefined;
		
	function getFormValues() {
		var dataObj = {};
		for (var i = 0; i < formFields.length; i++) {
			var field = formFields[i];
			
			var value = readFormValue(field);
			if (value === "")
				continue;
			dataObj[field] = $.trim(value);
		}
		
		return {
			action: "save",
			data: dataObj
		};
	}
	
	function addOrUpdateRow(data) {
		var html = data.html;
		var $row = findRow(data.rowId);
	
		if ($row[0]) {
			$row.replaceWith(html);
		} else {
			$lastElement = $updatingRepeat.children().last();
			if ($lastElement[0]) {
				$lastElement.after(html);
			} else {
				$updatingRepeat.html(html);
			}
		}
	};
	
	function removeRow(id) {	    
		var row = findRow(id);
	
		if (row[0]) {
			row.remove();
		}
	};
	
	function findRow(id) {
		return $updatingRepeat.find("[data-index='" + id + "']");
	}
	
	function readFormValue(name) {
		$el = $("[data-form-field='"+ name +"']");
		if ($el.is("input")) {
			return $el.val();
		}
		return $.trim($el.html());
	}
	
	function setFormValue(name, value) {
		$el = $("[data-form-field='"+ name +"']");
		if ($el.is("input")) {
			$el.val(value);
		}
		$el.html(value);
	}
	
	function getColumnValue($row, name) {
		return $.trim($row.find("[data-field='" + name +"']").html());
	}
	
	
	$(function () {
		$("[data-form-button=submit]").click(function() {
			socket.sendObjectMessage(getFormValues());
		});
		
		$("[data-form-button=clear]").click(function() {
			for (var i = 0; i < formFields.length; i++) {
				var field = formFields[i];
				setFormValue(field, "");
			}
			$("[data-form-button=submit]").val("Add");
		});
	});
	
	return {
		editRow: function (id) {
			var $row = findRow(id);
			for (var i = 0; i < formFields.length; i++) {
				var field = formFields[i];
				var value = getColumnValue($row, field);
				setFormValue(field, value);
			}
			$("[data-form-button=submit]").val("Update");
		},
		
		deleteRow: function (id) {
			socket.sendObjectMessage({
				action: "delete",
				rowId: id
			});
		},
		openSocket: function (websocketUrl, longPollUrl, _$updatingRepeat, _formFields) {
			formFields = _formFields;
			socket = liveSocket.open(websocketUrl, longPollUrl);
			$updatingRepeat = _$updatingRepeat;
			
			socket.onOpen = function() {
			    $("#connectionstate").html("Connected with " + socket.getType());
			};
			
			socket.onClose = function() {
				$("#connectionstate").html("Disconnected");		    
			};
			
			socket.onObjectMessage = function (obj) {
			    if (obj.action == "save") {
					addOrUpdateRow(obj);
			    } else if (obj.action == "delete") {
				    removeRow(obj.rowId);
			    }
			};
			
			socket.onError = function (evt) {
				var data = (evt.data) ? evt.data : evt.toSource();
				alert("Error: " + data);
			};
			
			socket.open();
		}
	};
})();