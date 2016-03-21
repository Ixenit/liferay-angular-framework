package com.ixenit.liferay.angular.portlet.exception;

public class NoSuchResourceMethodException extends RuntimeException {


	private static final long serialVersionUID = 1L;

	public NoSuchResourceMethodException() {
	}

	public NoSuchResourceMethodException(String message) {
		super(message);
	}

	public NoSuchResourceMethodException(Throwable cause) {
		super(cause);
	}

	public NoSuchResourceMethodException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSuchResourceMethodException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
