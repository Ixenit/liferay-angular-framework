// Init the common module
angular.module('liferay.common', []);

/**
 * 
 * @param configurations
 */
function bootstrap(configurations) {
	var portletId = configurations.portletId;

	// ---- Run Block

	run.$inject = [ '$rootScope',
		'portletUrl',
		'validator',
		'bootstrap3ElementModifier',
		'i18nErrorMessageResolver' 
	];

	function run($rootScope, portletUrl, validator, bootstrap3ElementModifier, i18nErrorMessageResolver) {

		// Calculate the actual portlet ID and put that in the root scope for all to use.
		// Remove the trailing '_' characters.
		$rootScope.portletId = portletId.substr(1, portletId.length - 2);

		// Put some Liferay stuff on the root scope to be used by our custom directive.
		$rootScope.liferay = {
			token : Liferay.authToken,
			companyId : Liferay.ThemeDisplay.getCompanyId(),
			loggedIn : Liferay.ThemeDisplay.isSignedIn()
		};

		$rootScope.$on('$stateChangeStart', function(event, toState) {
			// Test if the url was already calculated
			if (toState.hasOwnProperty('urlCalculated')) {
				return;
			}

			toState.templateUrl = portletUrl.createRenderUrl(toState.templateUrl);
			toState.urlCalculated = true;
		});

		bootstrap3ElementModifier.enableValidationStateIcons(true);

		validator.setErrorMessageResolver(i18nErrorMessageResolver.resolve);
	}

	// ---- Configuration Block

	config.$inject = [ '$urlRouterProvider',
		'$stateProvider',
		'$locationProvider',
		'portletUrlProvider',
		'NotificationProvider',
		'$translateProvider',
	];

	function config($urlRouterProvider, $stateProvider, $locationProvider, portletUrlProvider,
		NotificationProvider, $translateProvider) {

		var currentPageUrl = Liferay.ThemeDisplay.getLayoutURL();
		currentPageUrl = currentPageUrl.substr(currentPageUrl.indexOf('/', 7));

		$urlRouterProvider.otherwise(currentPageUrl);

		// ----- Register states here
		// https://github.com/angular-ui/ui-router/wiki

		var states = configurations.states || {};

		for ( var stateName in states) {
			$stateProvider.state(stateName, states[stateName]);
		}

		$locationProvider.html5Mode({
			enabled : true,
			// From https://docs.angularjs.org/api/ng/provider/$locationProvider:
			// rewriteLinks - {boolean} - (default: true) When html5Mode is enabled,
			// enables/disables url rewriting for relative links.
			rewriteLinks : false
		});

		// With this the our 'url' service can provide valid resource and render urls.
		portletUrlProvider.setPid(portletId);

		var locale = Liferay.ThemeDisplay.getBCP47LanguageId();
		var l10nResourceUrl = portletUrlProvider.$get().createResourceUrl('language', {
			'locale' : locale
		});

		$translateProvider.useUrlLoader(l10nResourceUrl);
		$translateProvider.preferredLanguage(locale);
		$translateProvider.useSanitizeValueStrategy('escapeParameters');

		NotificationProvider.setOptions({
			delay : 10000,
			startTop : 20,
			startRight : 10,
			verticalSpacing : 20,
			horizontalSpacing : 20,
			positionX : 'right',
			positionY : 'top'
		});
	}
	
	// ---- Bootstrapping
	var modules = configurations.modules || [];

	// Registering common modules
	modules.push('ui.router');
	modules.push('ui-notification');
	modules.push('jcs-autoValidate');
	modules.push('pascalprecht.translate');
	modules.push('liferay.common');

	var app = angular.module(portletId, modules);

	app.run(run);
	app.config(config);
	app.constant('portletId', portletId.substr(1, portletId.length - 2));

	// Run the portlet definied config and run methods
	if (configurations.run) {
		app.run(config.run);
	}

	if (configurations.config) {
		app.config(config.config);
	}

	// Ask liferay to render additional javascript codes before bootstrapping
	AUI().use('liferay-portlet-url', 'aui-base', 'liferay-service', function() {
		angular.bootstrap(document.getElementById(configurations.domId), [ portletId ]);
	});
}