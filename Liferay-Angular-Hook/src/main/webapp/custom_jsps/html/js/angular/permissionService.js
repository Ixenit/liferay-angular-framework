(function() {
	'use strict';

	angular.module('liferay.common').service('permissionService', permissionService);

	permissionService.$inject = [ 'liferayService', '$q', '$timeout' ];

	function permissionService(liferayService, $q, $timeout) {
		var PROCESSING = 'processing';
		var permissions = {};

		this.hasPermission = function(actionId) {
			var deferred = $q.defer();

			if (permissions.hasOwnProperty(actionId)) {
				// If the request is still processing then dont start another request for the same
				// actionId
				if (permissions[actionId] == PROCESSING) {
					retry(actionId, deferred);

					return deferred.promise;
				}

				// Othercase resolve by the retrieved value
				deferred.resolve(permissions[actionId]);

				return deferred.promise;
			}

			permissions[actionId] = PROCESSING;

			liferayService.callPortlet('has-permission', {
				actionId : actionId
			}).then(function(response) {
				// In case of success:
				var hasPermission = response.data[actionId];

				permissions[actionId] = hasPermission;

				deferred.resolve(permissions[actionId]);
			}, function(response) {
				// In case of error set it to false
				permissions[actionId] = false;

				deferred.resolve(permissions[actionId]);
			});

			return deferred.promise;
		};

		function retry(actionId, deferred) {
			$timeout(function() {
				if (permissions[actionId] != PROCESSING) {
					deferred.resolve(permissions[actionId]);

					return;
				}

				retry(actionId, deferred);
			}, 100);
		}

	}
})();