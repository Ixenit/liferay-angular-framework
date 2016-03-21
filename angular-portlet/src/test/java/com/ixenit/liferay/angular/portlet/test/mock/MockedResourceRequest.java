package com.ixenit.liferay.angular.portlet.test.mock;

import java.util.Map;

public class MockedResourceRequest extends BaseResourceRequest {

	private String resourceId;

	private Map<String, Object> parameterMap;

	public MockedResourceRequest(String resourceId, Map<String, Object> parameterMap) {
		this.resourceId = resourceId;
		this.parameterMap = parameterMap;
	}

	@Override
	public String getParameter(String name) {
		return String.valueOf(parameterMap.get(name));
	}

	@Override
	public String[] getParameterValues(String name) {
		return (String[])parameterMap.get(name);
	}

	@Override
	public String getResourceID() {
		return resourceId;
	}

}
