package com.ixenit.liferay.angular.portlet.test.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.WindowState;
import javax.servlet.http.Cookie;

public class BaseResourceRequest implements ResourceRequest {

	@Override
	public InputStream getPortletInputStream() throws IOException {
		return null;
	}

	@Override
	public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
	}

	@Override
	public BufferedReader getReader() throws UnsupportedEncodingException, IOException {
		return null;
	}

	@Override
	public String getCharacterEncoding() {
		return null;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public int getContentLength() {
		return 0;
	}

	@Override
	public String getMethod() {
		return null;
	}

	@Override
	public boolean isWindowStateAllowed(WindowState state) {
		return false;
	}

	@Override
	public boolean isPortletModeAllowed(PortletMode mode) {
		return false;
	}

	@Override
	public PortletMode getPortletMode() {
		return null;
	}

	@Override
	public WindowState getWindowState() {
		return null;
	}

	@Override
	public PortletPreferences getPreferences() {
		return null;
	}

	@Override
	public PortletSession getPortletSession() {
		return null;
	}

	@Override
	public PortletSession getPortletSession(boolean create) {
		return null;
	}

	@Override
	public String getProperty(String name) {
		return null;
	}

	@Override
	public Enumeration<String> getProperties(String name) {
		return null;
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		return null;
	}

	@Override
	public PortalContext getPortalContext() {
		return null;
	}

	@Override
	public String getAuthType() {
		return null;
	}

	@Override
	public String getContextPath() {
		return null;
	}

	@Override
	public String getRemoteUser() {
		return null;
	}

	@Override
	public Principal getUserPrincipal() {
		return null;
	}

	@Override
	public boolean isUserInRole(String role) {
		return false;
	}

	@Override
	public Object getAttribute(String name) {
		return null;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return null;
	}

	@Override
	public String getParameter(String name) {
		return null;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return null;
	}

	@Override
	public String[] getParameterValues(String name) {
		return null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return null;
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public void setAttribute(String name, Object o) {

	}

	@Override
	public void removeAttribute(String name) {

	}

	@Override
	public String getRequestedSessionId() {
		return null;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return false;
	}

	@Override
	public Locale getLocale() {
		return null;
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return null;
	}

	@Override
	public String getScheme() {
		return null;
	}

	@Override
	public String getServerName() {
		return null;
	}

	@Override
	public int getServerPort() {
		return 0;
	}

	@Override
	public String getWindowID() {
		return null;
	}

	@Override
	public Cookie[] getCookies() {
		return null;
	}

	@Override
	public Map<String, String[]> getPrivateParameterMap() {
		return null;
	}

	@Override
	public Map<String, String[]> getPublicParameterMap() {
		return null;
	}

	@Override
	public String getETag() {
		return null;
	}

	@Override
	public String getResourceID() {
		return null;
	}

	@Override
	public Map<String, String[]> getPrivateRenderParameterMap() {
		return null;
	}

	@Override
	public String getResponseContentType() {
		return null;
	}

	@Override
	public Enumeration<String> getResponseContentTypes() {
		return null;
	}

	@Override
	public String getCacheability() {
		return null;
	}

}
