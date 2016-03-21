package com.ixenit.liferay.angular.portlet;

import com.google.common.collect.Lists;
import com.ixenit.liferay.angular.portlet.annotation.ResourceContext;
import com.ixenit.liferay.angular.portlet.annotation.ResourceMethod;
import com.ixenit.liferay.angular.portlet.annotation.ResourceParam;
import com.ixenit.liferay.angular.portlet.parameter.FileWrapper;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PortalUtil;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

/**
 * This class is responsible to invoke a {@link ResourceMethod} annotated method and injecting the requested
 * parameters.
 *
 * @author Benjamin Hajnal
 *
 */
class ResourceMethodExecutor {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object process(Context context) throws Throwable {
		RequestWrapper requestWrapper = getRequest(context);

		Method method = context.getMethod();

		// TODO: this variables should be cached and computed only once for each method
		ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		String[] names = parameterNameDiscoverer.getParameterNames(method);
		Class[] types = method.getParameterTypes();
		Type[] genericParameterTypes = method.getGenericParameterTypes();

		Annotation[][] paramAnnotations = method.getParameterAnnotations();

		List<Object> parameterValues = Lists.<Object> newArrayList();

		// Iterating over the parameter list
		for (int i = 0; i < paramAnnotations.length; i++) {
			String name = names[i];
			Class type = types[i];
			Type genericType = genericParameterTypes[i];
			Annotation[] annotations = paramAnnotations[i];

			// Annotation is required on all of the parameters!
			if (annotations.length == 0) {
				throw new IllegalArgumentException("Parameter without annotation: " + type.getSimpleName()
						+ " " + name);
			}

			for (Annotation annotation : annotations) {

				if (annotation instanceof ResourceParam) {
					ResourceParam p = (ResourceParam) annotation;

					Object value = getValue(requestWrapper, name, type, genericType, p);

					parameterValues.add(value);
				} else if (annotation instanceof ResourceContext) {
					if (type.isAssignableFrom(ResourceRequest.class)) {
						parameterValues.add(context.getRequest());
					} else if (type.isAssignableFrom(ResourceResponse.class)) {
						parameterValues.add(context.getResponse());
					}
				}
			}
		}

		// Finally invoke the method with the created parameter list
		try {
			return method.invoke(context.getPortlet(), parameterValues.toArray());
		} catch (Exception e) {
			if (e instanceof InvocationTargetException) {
				throw e.getCause();
			}

			throw e;
		}
	}

	private RequestWrapper getRequest(Context context) {
		ResourceRequest resourceRequest = context.getRequest();

		ResourceMethod resourceAnnotation = context.getMethod().getAnnotation(ResourceMethod.class);

		// For multipart request the parameters could be fetched from the UploadRequest
		if (resourceAnnotation.multipart()) {
			return new RequestWrapper(PortalUtil.getUploadPortletRequest(resourceRequest));
		}

		return new RequestWrapper(resourceRequest);
	}

	/**
	 * Retrieves the value for a parameter from the request 'parametermap'.
	 *
	 * <br>
	 * Example: exmapleMethod(&#064;Param List&lt;parameterizedType&gt; listParam, &#064;Param type
	 * normalParam)
	 *
	 * @param request
	 *            the actual request with parameters
	 * @param name
	 *            the name of the parameter
	 * @param type
	 *            the Java type of the parameter
	 * @param parameterizedType
	 *            the parameterized type of the parameter to calculate List's generic type
	 * @param param
	 *            the Param annotation of the parameter
	 * @return the value of the parameter to inject into the method
	 */
	private Object getValue(RequestWrapper request, String name, Class<?> type, Type parameterizedType,
			ResourceParam param) {

		Object value = null;

		if (List.class.equals(type)) {

			// The List<T> T type computed from the method signature
			Type genericType = ((ParameterizedType) parameterizedType).getActualTypeArguments()[0];

			Object[] parameterListValues = null;

			if (FileWrapper.class.equals(genericType)) {
				File[] files = request.getFiles(name);
				String[] fileNames = request.getFileNames(name);

				if (files == null || files.length == 0) {
					if (param.required()) {
						throw new IllegalArgumentException("Required parameter without value: " + name);
					}

					return Collections.emptyList();
				}

				parameterListValues = new Object[files.length];

				for (int j = 0; j < files.length; j++) {
					parameterListValues[j] = new FileWrapper(files[j], fileNames[j]);
				}
			} else {
				String[] parameterStringValues = request.getParameterValues(name);

				if (parameterStringValues == null || isEmpty(parameterStringValues)) {
					if (param.required()) {
						throw new IllegalArgumentException("Required parameter without value: " + name);
					}

					return Collections.emptyList();
				}

				parameterListValues = new Object[parameterStringValues.length];

				// Convert all of the items from the input array to the List's generic type and store
				// them in an Object array
				if (String.class.equals(genericType)) {
					parameterListValues = parameterStringValues;
				} else if (Integer.class.equals(genericType)) {
					for (int j = 0; j < parameterStringValues.length; j++) {
						parameterListValues[j] = Integer.parseInt(parameterStringValues[j]);
					}
				} else if (Long.class.equals(genericType)) {
					for (int j = 0; j < parameterStringValues.length; j++) {
						parameterListValues[j] = Long.parseLong(parameterStringValues[j]);
					}
				} else if (Double.class.equals(genericType)) {
					for (int j = 0; j < parameterStringValues.length; j++) {
						parameterListValues[j] = Double.parseDouble(parameterStringValues[j]);
					}
				} else if (Boolean.class.equals(genericType)) {
					for (int j = 0; j < parameterStringValues.length; j++) {
						parameterListValues[j] = parseBoolean(parameterStringValues[j]);
					}
				}
			}

			// Convert the array with typed items to a List -> this ensures that all of the items
			// will be properly typed when the resource method uses them
			value = Arrays.asList(parameterListValues);
		} else {

			if (FileWrapper.class.equals(type)) {
				File file = request.getFile(name);
				String fileName = request.getFileName(name);

				if (file == null) {
					if (param.required()) {
						throw new IllegalArgumentException("Required parameter without value: " + name);
					}

					return null;
				}

				value = new FileWrapper(file, fileName);
			} else {
				String paramStringValue = request.getParameter(name);

				if (Validator.isNull(paramStringValue)) {
					if (param.required()) {
						throw new IllegalArgumentException("Required parameter without value: " + name);
					}

					return null;
				}

				if (String.class.equals(type)) {
					value = paramStringValue;
				} else if (Integer.class.equals(type)) {
					value = Integer.parseInt(paramStringValue);
				} else if (Long.class.equals(type)) {
					value = Long.parseLong(paramStringValue);
				} else if (Double.class.equals(type)) {
					value = Double.parseDouble(paramStringValue);
				} else if (Boolean.class.equals(type)) {
					value = parseBoolean(paramStringValue);
				} else if (DateTime.class.equals(type)) {
					String dateFormat = param.dateFormat();

					if (Validator.isNull(dateFormat)) {
						value = new DateTime(Long.parseLong(paramStringValue));
					} else {
						value = DateTimeFormat.forPattern(dateFormat).parseDateTime(paramStringValue);
					}
				} else {
					throw new IllegalStateException("Parameters type is not supported!");
				}
			}
		}

		return value;
	}

	private boolean parseBoolean(String value) {
		String trimedValue = value.toLowerCase().trim();

		return "true".equals(trimedValue) || "1".equals(trimedValue);
	}

	private class RequestWrapper {

		private static final String MULTIPART_ERROR_MSG =
				"For file upload use multipart form with multipart @ResourceMethod annotation";

		private UploadPortletRequest uploadPortletRequest;

		private PortletRequest portletRequest;

		public RequestWrapper(UploadPortletRequest uploadPortletRequest) {
			this.uploadPortletRequest = uploadPortletRequest;
		}

		public RequestWrapper(PortletRequest portletRequest) {
			this.portletRequest = portletRequest;
		}

		public String[] getParameterValues(String name) {
			if (uploadPortletRequest != null) {
				return uploadPortletRequest.getParameterValues(name);
			} else {
				return portletRequest.getParameterValues(name);
			}
		}

		public String getParameter(String name) {
			if (uploadPortletRequest != null) {
				return uploadPortletRequest.getParameter(name);
			} else {
				return portletRequest.getParameter(name);
			}
		}

		public File getFile(String name) {
			if (uploadPortletRequest == null) {
				throw new IllegalArgumentException(MULTIPART_ERROR_MSG);
			}

			return uploadPortletRequest.getFile(name);
		}

		public String getFileName(String name) {
			if (uploadPortletRequest == null) {
				throw new IllegalArgumentException(MULTIPART_ERROR_MSG);
			}

			return uploadPortletRequest.getFileName(name);
		}

		public String[] getFileNames(String name) {
			if (uploadPortletRequest == null) {
				throw new IllegalArgumentException(MULTIPART_ERROR_MSG);
			}

			return uploadPortletRequest.getFileNames(name);
		}

		public File[] getFiles(String name) {
			if (uploadPortletRequest == null) {
				throw new IllegalArgumentException(MULTIPART_ERROR_MSG);
			}

			return uploadPortletRequest.getFiles(name);
		}

	}

	public static boolean isEmpty(String[] array) {
		for (String tmp : array) {

			if (Validator.isNotNull(tmp)) {
				return false;
			}
		}

		return true;
	}
}