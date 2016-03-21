(function() {
	'use strict';

	angular.module('liferay.common').service('liferayService', liferayService);

	liferayService.$inject = [ '$q', '$http', 'portletUrl', 'Notification', '$translate' ];

	function liferayService($q, $http, portletUrl, Notification, $translate) {

		this.callService = function(url, data) {
			var deferred = $q.defer();

			Liferay.Service(url, data, function(obj) {
				deferred.resolve(obj);
			});

			return deferred.promise;
		};

		this.callPortlet = function(resourceId, parameters, success) {
			var deferred = $q.defer();

			var resource = portletUrl.createResourceUrl(resourceId, parameters);

			$http.get(resource).success(function(data, status, headers, config) {
				// The angular portlet should always send a status and an
				// optional data
				if (data.hasOwnProperty("status")) {
					if (success) {
						success(data);
					}

					return deferred.resolve(data);
				}

				showErrorMessage();

				deferred.reject(data);
			}).error(function(data, status, headers, config) {
				showErrorMessage(data.message);

				deferred.reject(data);
			});

			return deferred.promise;
		};

		function showErrorMessage(msg) {
			var message = msg || 'unknown-error';

			$translate(message).then(function(v) {
				Notification.error(v);
			});
		}
	}
})();
