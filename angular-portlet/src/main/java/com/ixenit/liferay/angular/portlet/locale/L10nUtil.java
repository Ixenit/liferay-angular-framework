package com.ixenit.liferay.angular.portlet.locale;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * The class is responsible to serve the values for language keys. It loads the bundle from 2 places
 *
 * <ul>
 * <li>1. Liferay's default locale bundle - <b>it is loaded only 1 time!</b></li>
 * <li>2. The portlet's Language.properties file if exists - <b>cached</b></li>
 * </ul>
 *
 *
 * @author Benjamin Hajnal
 *
 */
public final class L10nUtil {

	private static final String BUNDLE_ENCODING = "UTF8";

	private static int CACHE_TIMEOUT = 10;

	private static final LoadingCache<String, Map<String, String>> CACHE = CacheBuilder.newBuilder()
			.maximumSize(5).expireAfterWrite(CACHE_TIMEOUT, TimeUnit.MINUTES)
			.build(new CacheLoaderExtension());

	private static final String PORTLET_LANGUAGE_PROPERTIES = "/content/Language.properties";

	private static final Log logger = LogFactoryUtil.getLog(L10nUtil.class.getName());

	/**
	 * Retrieves the value for a language key. The order is the following:
	 *
	 * <ul>
	 * <li>1. Liferay's default bundle for the locale</li>
	 * <li>2. portlet: content/Language.properties (if exists)</li>
	 * </ul>
	 *
	 * @param locale
	 *            the locale to get the key
	 * @param key
	 *            the key for the value
	 * @return the value for the key in the given locale
	 */
	public static String get(String locale, String key) {
		return getBundle(locale).get(key);
	}

	/**
	 * Gets the whole localization bundle for a locale. The order of the keys are the following:
	 *
	 * <ul>
	 * <li>1. Liferay's default bundle for the locale</li>
	 * <li>2. portlet: content/Language.properties (if exists)</li>
	 * </ul>
	 *
	 * @param locale
	 *            the locale of the bundle
	 * @return the cached bundle
	 */
	public static Map<String, String> getBundle(String locale) {
		try {
			return CACHE.get(locale);
		} catch (ExecutionException e) {
			logger.warn("Failed to load bundle.", e);
		}

		return Collections.emptyMap();
	}

	/**
	 * Loads Liferay's own Language bundle. It contains the default Language.properties bundle language
	 * (overwritten by the hooks).
	 *
	 * @param locale
	 *            the locale of the bundle
	 * @return a map containing the language key value pairs
	 */
	private static Map<String, String> loadLiferayBundle(String locale) throws Exception {

		// FIXME: this solution could broke with Liferay's version changes

		ClassLoader portalClassLoader = PortalClassLoaderUtil.getClassLoader();
		Class<?> c = portalClassLoader.loadClass("com.liferay.portal.language.LanguageResources");
		Field f = c.getDeclaredField("_languageMaps");
		f.setAccessible(true);

		@SuppressWarnings("unchecked")
		Map<Locale, Map<String, String>> bundles = (Map<Locale, Map<String, String>>) f.get(null);

		Locale localeValue = Locale.forLanguageTag(locale);

		return bundles.get(localeValue);
	}

	/**
	 * Loads the content of /content/Language.properties from the actual portlet then loads the
	 * LapkerLanguage.properties from the tomcat's template folder overwriting the existing keys.
	 *
	 * @param locale
	 *            the locale of the bundle
	 * @return a map containing the language key value pairs
	 */
	private static Map<String, String> loadPortletBundle(String locale) {
		Properties properties = new Properties();

		// -- Load the portlet's Language.properties

		try {

			// FIXME: locale should be used to load the proper bundle

			InputStream bundleResource = L10nUtil.class.getResourceAsStream(PORTLET_LANGUAGE_PROPERTIES);

			properties.load((new InputStreamReader(bundleResource, BUNDLE_ENCODING)));
		} catch (Exception e) {
			logger.warn("Failed to load the portlet's language properties:" + e.getMessage());

			return Collections.emptyMap();
		}

		// -- Convert the bundle into a map

		Map<String, String> languageBundle = new HashMap<>();

		for (String key : properties.stringPropertyNames()) {
			String value = properties.getProperty(key);

			languageBundle.put(key, value);
		}

		return languageBundle;
	}

	private static final class CacheLoaderExtension extends CacheLoader<String, Map<String, String>> {

		@Override
		public Map<String, String> load(String locale) {
			if (logger.isDebugEnabled()) {
				logger.debug("Reloading localization cache for locale: " + locale);
			}

			Map<String, String> portletBundle = loadPortletBundle(locale);

			try {
				Map<String, String> liferayBundle = loadLiferayBundle(locale);

				// Overwrite the liferay values with the custom values
				liferayBundle.putAll(portletBundle);

				return liferayBundle;
			} catch (Exception e) {
				logger.warn("Failed to load Liferay bundle. Using only portlet bundle", e);

				return portletBundle;
			}

		}
	}
}
