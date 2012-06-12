/**
 * @author Mark van der Tol
 */
var storage = (function () {
	function register($el, storageContainer) {
		if (!storageContainer)
			return;
		
		var $form = $el.closest("form");
		var storageKey = "storage_" + $el.attr('id');
		
		restoreStoredValue($el, storageKey, storageContainer);
		
		$form.submit(function () {
			storageContainer.setItem(storageKey, $el.val());
		});
	}
	
	function restoreStoredValue($el, storageKey, storageContainer) {
		var value = storageContainer.getItem(storageKey);
		
		if (value != null) { //if value is present, then restore value
			$el.val(value);
		}
	}
	
	return {
		registerSessionStorageElement: function ($el, storageContainer) {
			register($el, storageContainer);
		},
		registerSessionStorageElement: function ($el) {
			register($el, sessionStorage);
		},
		registerLocalStorageElement: function ($el) {
			register($el, localStorage);
		}
	};
})();