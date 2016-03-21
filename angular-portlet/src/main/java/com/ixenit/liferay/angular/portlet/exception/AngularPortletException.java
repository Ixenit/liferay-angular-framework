package com.ixenit.liferay.angular.portlet.exception;

import com.ixenit.liferay.angular.portlet.AngularPortlet;

/**
 * This exception should be thrown if we want to display the exception message
 * to the user in the angular application.
 *
 * @see AngularPortlet
 *
 * @author Benjamin Hajnal
 *
 */
public class AngularPortletException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AngularPortletException(String message) {
		super(message);
	}

}
