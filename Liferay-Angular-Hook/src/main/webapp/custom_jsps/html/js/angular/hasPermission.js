(function() {
	'use strict';

	angular.module('liferay.common').directive('hasPermission', hasPermission);

	hasPermission.$inject = [ 'permissionService' ];

	function hasPermission(permissionService) {

		function link(scope, element, attrs) {
			element.hide();

			var actionKey = attrs.hasPermission.trim();
			var invert = actionKey.startsWith("!");

			if (invert) {
				actionKey = actionKey.substring(1).trim();
			}

			permissionService.hasPermission(actionKey).then(function(hasPermission) {
				if ((invert && !hasPermission) || (!invert && hasPermission)) {
					element.show();
				}
			});
		}

		// ----- Public API

		return {
			restrict : 'A',
			link : link
		};
	}
})();