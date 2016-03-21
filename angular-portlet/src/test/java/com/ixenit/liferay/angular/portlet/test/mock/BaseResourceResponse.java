package com.ixenit.liferay.angular.portlet.test.mock;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.portlet.CacheControl;
import javax.portlet.PortletURL;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.servlet.http.Cookie;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

public class BaseResourceResponse implements ResourceResponse {

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public void setContentType(String type) {

	}

	@Override
	public String getCharacterEncoding() {
		return null;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return null;
	}

	@Override
	public Locale getLocale() {
		return null;
	}

	@Override
	public void setBufferSize(int size) {

	}

	@Override
	public int getBufferSize() {
		return 0;
	}

	@Override
	public void flushBuffer() throws IOException {

	}

	@Override
	public void resetBuffer() {

	}

	@Override
	public boolean isCommitted() {
		return false;
	}

	@Override
	public void reset() {

	}

	@Override
	public OutputStream getPortletOutputStream() throws IOException {
		return null;
	}

	@Override
	public CacheControl getCacheControl() {
		return null;
	}

	@Override
	public void addProperty(String key, String value) {

	}

	@Override
	public void setProperty(String key, String value) {

	}

	@Override
	public String encodeURL(String path) {
		return null;
	}

	@Override
	public String getNamespace() {
		return null;
	}

	@Override
	public void addProperty(Cookie cookie) {

	}

	@Override
	public void addProperty(String key, Element element) {

	}

	@Override
	public Element createElement(String tagName) throws DOMException {
		return null;
	}

	@Override
	public void setLocale(Locale loc) {

	}

	@Override
	public void setCharacterEncoding(String charset) {

	}

	@Override
	public void setContentLength(int len) {

	}

	@Override
	public PortletURL createRenderURL() {
		return null;
	}

	@Override
	public PortletURL createActionURL() {
		return null;
	}

	@Override
	public ResourceURL createResourceURL() {
		return null;
	}

}
