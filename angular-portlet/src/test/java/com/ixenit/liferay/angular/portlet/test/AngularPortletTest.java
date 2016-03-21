package com.ixenit.liferay.angular.portlet.test;

import com.ixenit.liferay.angular.portlet.AngularPortlet;
import com.ixenit.liferay.angular.portlet.annotation.ResourceMethod;
import com.ixenit.liferay.angular.portlet.annotation.ResourceParam;
import com.ixenit.liferay.angular.portlet.test.mock.MockedResourceRequest;
import com.ixenit.liferay.angular.portlet.test.mock.MockedResourceResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.portlet.PortletException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Benjamin Hajnal
 *
 */
public class AngularPortletTest {

	private static final String STRING_PARAM = "imastring";

	private static final String[] STRING_LIST_PARAM = { "stirng1", "string2" };

	private static final Integer INTEGER_PARAM = 42;

	private static final Integer[] INTEGER_LIST_PARAM = { 1, 2 };

	private static final Long LONG_PARAM = 2131231321332312L;

	private static final Long[] LONG_LIST_PARAM = { 11111111L, 2222222L };

	private static final Double DOUBLE_PARAM = 2.5;

	private static final Double[] DOUBLE_LIST_PARAM = { 1.2, 31.2 };

	private static final Boolean BOOLEAN_PARAM = false;

	private static final Boolean[] BOOLEAN_LIST_PARAM = { true, false };

	private static final DateTime DATE_TIME_PARAM = new DateTime();

	private AngularPortlet portlet = new AngularPortletExtension();

	@Test
	public void testBaseTypedParameters() throws IOException, PortletException {
		HashMap<String, Object> parameters = new HashMap<>();

		parameters.put("stringParam", STRING_PARAM.toString());
		parameters.put("integerParam", INTEGER_PARAM.toString());
		parameters.put("longParam", LONG_PARAM.toString());
		parameters.put("doubleParam", DOUBLE_PARAM.toString());
		parameters.put("booleanParam", BOOLEAN_PARAM.toString());
		parameters.put("dateTimeParam", String.valueOf(DATE_TIME_PARAM.getMillis()));
		parameters.put("formattedDateTimeParam",
				DateTimeFormat.forPattern("yyyy.MM.dd HH:mm:ss,SSS").print(DATE_TIME_PARAM));

		MockedResourceRequest resourceRequest = new MockedResourceRequest("test-base-types", parameters);

		portlet.serveResource(resourceRequest, new MockedResourceResponse());
	}

	@Test
	public void testListTypedParameters() throws IOException, PortletException {
		HashMap<String, Object> parameters = new HashMap<>();

		parameters.put("stringListParam", STRING_LIST_PARAM);
		parameters.put("integerListParam", getArray(INTEGER_LIST_PARAM));
		parameters.put("longListParam", getArray(LONG_LIST_PARAM));
		parameters.put("doubleListParam", getArray(DOUBLE_LIST_PARAM));
		parameters.put("booleanListParam", getArray(BOOLEAN_LIST_PARAM));

		MockedResourceRequest resourceRequest = new MockedResourceRequest("test-list-types", parameters);

		portlet.serveResource(resourceRequest, new MockedResourceResponse());
	}

	@Test
	public void testOptionalParameters() throws IOException, PortletException {
		HashMap<String, Object> parameters = new HashMap<>();

		MockedResourceRequest resourceRequest = new MockedResourceRequest("test-optional-parameter",
				parameters);

		portlet.serveResource(resourceRequest, new MockedResourceResponse());
	}

	@Test
	public void testMissingParameters() throws IOException, PortletException {
		HashMap<String, Object> parameters = new HashMap<>();

		MockedResourceRequest resourceRequest = new MockedResourceRequest("test-missing-parameter",
				parameters);

		portlet.serveResource(resourceRequest, new MockedResourceResponse());
	}

	private String[] getArray(Object[] array) {
		String[] stringArray = new String[array.length];

		for (int i = 0; i < array.length; i++) {
			stringArray[i] = array[i].toString();
		}

		return stringArray;
	}

	public final class AngularPortletExtension extends AngularPortlet {

		@Override
		protected String getResourceName() {
			return "test.portlet";
		}

		@ResourceMethod("test-base-types")
		public void testBaseParamTypes(@ResourceParam String stringParam,
				@ResourceParam Integer integerParam, @ResourceParam Long longParam,
				@ResourceParam Double doubleParam, @ResourceParam Boolean booleanParam,
				@ResourceParam DateTime dateTimeParam,
				@ResourceParam(dateFormat = "yyyy.MM.dd HH:mm:ss,SSS") DateTime formattedDateTimeParam) {

			Assert.assertEquals(stringParam, STRING_PARAM);
			Assert.assertEquals(integerParam, INTEGER_PARAM);
			Assert.assertEquals(longParam, LONG_PARAM);
			Assert.assertEquals(doubleParam, DOUBLE_PARAM);
			Assert.assertEquals(booleanParam, BOOLEAN_PARAM);
			Assert.assertEquals(dateTimeParam, DATE_TIME_PARAM);
			Assert.assertEquals(formattedDateTimeParam, DATE_TIME_PARAM);
		}

		@ResourceMethod("test-list-types")
		public void testListParamTypes(@ResourceParam List<String> stringListParam,
				@ResourceParam List<Integer> integerListParam, @ResourceParam List<Long> longListParam,
				@ResourceParam List<Double> doubleListParam, @ResourceParam List<Boolean> booleanListParam) {

			Assert.assertEquals(stringListParam, Arrays.asList(STRING_LIST_PARAM));
			Assert.assertEquals(integerListParam, Arrays.asList(INTEGER_LIST_PARAM));
			Assert.assertEquals(longListParam, Arrays.asList(LONG_LIST_PARAM));
			Assert.assertEquals(doubleListParam, Arrays.asList(DOUBLE_LIST_PARAM));
			Assert.assertEquals(booleanListParam, Arrays.asList(BOOLEAN_LIST_PARAM));
		}

		@ResourceMethod("test-optional-parameter")
		public void testOptionalParameters(@ResourceParam(required = false) List<String> optionalList,
				@ResourceParam(required = false) String optionalParam) {

			Assert.assertEquals(optionalList.size(), 0);
			Assert.assertNull(optionalParam);
		}

		@ResourceMethod("test-missing-parameter")
		public void testMissingParameters(@ResourceParam List<String> optionalList) {
			// It shouldn't be called
			Assert.fail("The method should not be called!");
		}

	}

}
