(function() {
	'use strict';

	angular.module('liferay.common').provider('portletUrl', portletUrl);

	function portletUrl() {
		var self = this;

		this.pid = '';

		this.setPid = function(pid) {
			self.pid = pid.substr(1, pid.length - 2);
		};

		this.$get = function() {

			// ---- Private implementation
			
			function createRenderUrl(page) {
				var resourceURL = Liferay.PortletURL.createRenderURL();
				resourceURL.setPortletId(self.pid);
				resourceURL.setPortletMode('view');
				resourceURL.setWindowState('exclusive');
				resourceURL.setParameter('jspPage', '/partials/' + page + '.html');

				return resourceURL.toString();
			}

			function createResourceUrl(resourceId, params, portletId) {
				var resourceURL = Liferay.PortletURL.createResourceURL();
				resourceURL.setPortletId(portletId || self.pid);
				resourceURL.setResourceId(resourceId);

				params = params || {};

				for ( var key in params) {
					resourceURL.setParameter(key, params[key]);
				}

				return resourceURL.toString();
			}

			// ---- Public API

			return {
				createRenderUrl : createRenderUrl,
				createResourceUrl : createResourceUrl
			};
		};
	}
})();