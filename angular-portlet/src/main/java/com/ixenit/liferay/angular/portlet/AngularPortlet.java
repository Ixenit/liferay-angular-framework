package com.ixenit.liferay.angular.portlet;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ixenit.liferay.angular.portlet.annotation.ResourceCache;
import com.ixenit.liferay.angular.portlet.annotation.ResourceContext;
import com.ixenit.liferay.angular.portlet.annotation.ResourceMethod;
import com.ixenit.liferay.angular.portlet.annotation.ResourceParam;
import com.ixenit.liferay.angular.portlet.annotation.ResourcePermissions;
import com.ixenit.liferay.angular.portlet.exception.AngularPortletException;
import com.ixenit.liferay.angular.portlet.exception.NoSuchResourceMethodException;
import com.ixenit.liferay.angular.portlet.locale.L10nUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

/**
 * The {@link AngularPortlet} class provides a default implementation for communication with an AngularJs
 * client.
 *
 * The subclasses could use the following method and method parameter annotations:
 * <ul>
 * <li>{@link ResourceMethod}: to sign the resourceId on a method</li>
 * <li>{@link ResourceCache}: to cache the response by a param key's value</li>
 * <li>{@link ResourcePermissions}: to check for permissions for the given user</li>
 * <li>{@link ResourceContext}: to request the current ResourceRequest or ResourceResponse as a parameter</li>
 * <li>{@link ResourceParam}: to inject the related parameter from the requests data</li>
 * </ul>
 *
 * <p>
 * Every {@link ResourceMethod} annotated method should have all of its parameter annotated by
 * {@link ResourceContext} or the {@link ResourceParam} other case {@link IllegalArgumentException} will be
 * thrown.
 * </p>
 *
 * <p>
 * The return value will be serialized to json by GSON. It could be any subclass of Object.
 * </p>
 *
 * <p>
 * {@link AngularPortletException} could be thrown from the portlet to send the error message to the client.
 * </p>
 *
 * <h3>Example:</h3>
 *
 * <pre>
 * &#64;ResourceMethod("get-all-user-for-company")
 * &#64;ResourceCache(key = "companyId")
 * &#64;ResourcePermissions({ "VIEW_USERS_FOR_COMPANY" })
 * public List&lt;User&gt; getCompanyUsers(
 * 	&#64;ResourceContext ResourceRequest request, &#64;ResourceParam Long companyId,
 * 	&#64;ResourceParam Integer from, &#64;ResourceParam Integer to)
 * 	throws Exception {
 *
 * 	return UserLocalServiceUtil.getCompanyUsers(companyId, from, to);
 * }
 *
 * <pre>
 *
 * @author Benjamin Hajnal
 *
 */
public abstract class AngularPortlet extends MVCPortlet {

	private static final String RESPONES_ERROR = "error";

	private static final String RESPONSE_OK = "ok";

	private static final String ACCES_DENIED = "acces-denied";

	private static final String UNKNOWN_ERROR = "unknown-error";

	private static final Object NULL_RESOURCE_RESPONSE = new Object();

	protected static final Gson GSON = new GsonBuilder().setFieldNamingStrategy(
			new LiferayFieldNamingStrategy()).create();

	private static final ResourceMethodExecutor METHOD_EXECUTOR = new ResourceMethodExecutor();

	// This cache stores responses of methods which are marked cachable
	private final Cache<Context, Object> resourceResponseCache;

	private final Log logger = LogFactoryUtil.getLog(getClass());

	public AngularPortlet(int cacheSize, int cacheExpireMinutes) {

		// Prepare the response cache with the given parameters
		this.resourceResponseCache = prepareCache(cacheSize, cacheExpireMinutes);
	}

	public AngularPortlet() {

		// Prepare the response cache
		this.resourceResponseCache = this.prepareCache();
	}

	@Override
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws IOException, PortletException {

		Response response = null;

		try {

			Object responseObject = handleResourceRequest(resourceRequest, resourceResponse);

			response = new Response(RESPONSE_OK, null, responseObject);
		} catch (Throwable e) {

			// For jUnit tests this exception should be thrown
			if (e instanceof AssertionError) {
				throw (AssertionError) e;
			}

			getLogger().error("Resource request failed!", e);

			String msg = UNKNOWN_ERROR;

			if (e instanceof AngularPortletException) {
				msg = e.getMessage();
			}

			response = new Response(RESPONES_ERROR, msg, null);
			resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, "400");
		}

		String json = GSON.toJson(response);

		resourceResponse.getWriter().print(json);

		// Sending no-cache headers to prevent IE or proxies to cache an AJAX response
		resourceResponse.setProperty("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		resourceResponse.setProperty("Pragma", "no-cache"); // HTTP 1.0.
		resourceResponse.setProperty("Expires", "0"); // Proxies.
	}

	/**
	 * Returns a hashmap with a pair containing the actionId key and a bool value, true if the current user
	 * have permission for the action, false if not.
	 *
	 * @param request
	 *            the given resource request
	 * @param actionId
	 *            the actionId of the request we ask the permission for
	 * @return if the user has the permission to the given actionId
	 */
	@ResourceMethod("has-permission")
	public Object hasPermission(@ResourceContext ResourceRequest request, @ResourceParam String actionId) {
		boolean hasPermission = hasPermissionInternal(request, actionId);

		Map<Object, Object> response = new HashMap<>();
		response.put(actionId, hasPermission);

		return response;
	}

	@ResourceMethod("language")
	@ResourceCache(key = "locale")
	public Map<String, String> getLanguage(@ResourceParam String locale) throws Exception {
		return L10nUtil.getBundle(locale);
	}

	protected boolean hasPermissionInternal(ResourceRequest request, String actionId) {
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

		long groupId = themeDisplay.getScopeGroupId();
		String resourceName = getResourceName();

		PermissionChecker permissionChecker = themeDisplay.getPermissionChecker();

		return permissionChecker.hasPermission(groupId, resourceName, groupId, actionId);
	}

	@Override
	protected void checkPath(String path) throws PortletException {

		// Allow partials dir
		if (path.startsWith("/partials/")) {
			return;
		}

		super.checkPath(path);
	}

	@Override
	protected void doDispatch(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException,
			PortletException {

		String path = getPath(renderRequest);

		if (path != null && path.startsWith("/partials/")) {

			// TODO: The permission for the partial here could be computed
			include(path, renderRequest, renderResponse);
		} else {
			super.doDispatch(renderRequest, renderResponse);
		}

	}

	protected abstract String getResourceName();

	protected final Log getLogger() {
		return logger;
	}

	/**
	 * Create a {@link Cache} here that will cache responses to resource requests marked as cachable with
	 * {@link ResourceCache}. This cache is common among resource methods of a portlet, so subclasses should
	 * set a cache size that takes the number of cachable methods into account.
	 *
	 * The default implementation uses cache size of 64 and expiration of 10 minutes.
	 *
	 * @return the cache built by the default settings
	 */
	protected Cache<Context, Object> prepareCache() {
		return prepareCache(64, 10);
	}

	private Cache<Context, Object> prepareCache(int cacheSize, int cacheExpireMinutes) {
		return CacheBuilder.newBuilder().maximumSize(cacheSize)
				.expireAfterWrite(cacheExpireMinutes, TimeUnit.MINUTES).build();
	}

	/**
	 * Dispatch resource requests to the actual methods based on the specified resourceId on the request.
	 */
	private Object handleResourceRequest(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Throwable {

		String resourceId = resourceRequest.getResourceID();

		Method resourceMethod = getResourceMethod(resourceId);

		checkPermissions(resourceRequest, resourceMethod);

		return evaluateRequest(resourceRequest, resourceResponse, resourceId, resourceMethod);
	}

	private Method getResourceMethod(String resourceId) {
		Optional<Method> method = findMatchingResourceMethod(resourceId);

		if (!method.isPresent()) {
			String message = "No matching resource method found for ID: " + resourceId + " in class "
					+ getClass().getCanonicalName();

			throw new NoSuchResourceMethodException(message);
		}

		return method.get();
	}

	private Object evaluateRequest(ResourceRequest resourceRequest, ResourceResponse resourceResponse,
			String resourceId, Method resourceMethod) throws Throwable {

		Context context = new Context(this, resourceRequest, resourceResponse, resourceMethod, resourceId);

		ResourceCache cache = resourceMethod.getAnnotation(ResourceCache.class);

		// The resource should not be cached
		if (cache == null) {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("Get resource " + resourceId + " without cache.");
			}

			return METHOD_EXECUTOR.process(context);
		}

		String param = resourceRequest.getParameter(cache.key());
		context.setParamValue(param);

		if (getLogger().isDebugEnabled()) {
			getLogger().debug("Get resource " + resourceId + " with ID " + param + " from cache.");
		}

		Object response = resourceResponseCache.getIfPresent(context);

		// If the value for the context in the cache is our singleton then the
		// method has evaulated to null before
		if (NULL_RESOURCE_RESPONSE.equals(response)) {
			return null;
		}

		if (response == null) {
			response = METHOD_EXECUTOR.process(context);

			// If the cached method has evaulated to null then we should use our
			// singleton to prevent NPE from guava cache
			if (response == null) {
				response = NULL_RESOURCE_RESPONSE;
			}

			resourceResponseCache.put(context, response);
		}

		return response;
	}

	/**
	 * Checks the permissions on the resource request if it's specified by {@link ResourcePermissions}
	 * annotation.
	 *
	 * @param resourceRequest
	 * @param resourceMethod
	 */
	private void checkPermissions(ResourceRequest resourceRequest, Method resourceMethod) {
		ResourcePermissions permissions = resourceMethod.getAnnotation(ResourcePermissions.class);

		// If the annotation is not present
		if (permissions == null) {
			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);

		// If the method is only for guests (e.g.: login)
		if (permissions.guestOnly() && themeDisplay.isSignedIn()) {
			throw new AngularPortletException(ACCES_DENIED);
		}

		// Empty permission array means that this function can be used by anyone
		if (permissions.value().length == 0) {
			return;
		}

		long groupId = themeDisplay.getScopeGroupId();
		String resourceName = getResourceName();

		PermissionChecker permissionChecker = themeDisplay.getPermissionChecker();

		boolean hasPermission = false;

		for (String actionId : permissions.value()) {
			if (permissionChecker.hasPermission(groupId, resourceName, groupId, actionId)) {
				hasPermission = true;
			}
		}

		if (!hasPermission) {
			throw new AngularPortletException(ACCES_DENIED);
		}
	}

	private Optional<Method> findMatchingResourceMethod(String resourceId) {
		for (Method method : this.getClass().getMethods()) {
			for (Annotation annotation : method.getAnnotations()) {
				if (annotation.annotationType() == ResourceMethod.class) {
					ResourceMethod resource = (ResourceMethod) annotation;
					String id = resource.value();

					if (resourceId.equals(id)) {
						return Optional.of(method);
					}
				}
			}
		}

		return Optional.absent();
	}

	private static class LiferayFieldNamingStrategy implements FieldNamingStrategy {

		@Override
		public String translateName(Field field) {
			String fieldName = FieldNamingPolicy.IDENTITY.translateName(field);

			if (fieldName.startsWith("_")) {
				fieldName = fieldName.substring(1);
			}

			return fieldName;
		}

	}

	private class Response {

		@SuppressWarnings("unused")
		private String status;

		@SuppressWarnings("unused")
		private String message;

		@SuppressWarnings("unused")
		private Object data;

		public Response(String status, String message, Object data) {
			this.status = status;
			this.message = message;
			this.data = data;
		}

	}
}