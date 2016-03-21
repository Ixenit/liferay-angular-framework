package com.ixenit.liferay.angular.portlet.annotation;

import com.ixenit.liferay.angular.portlet.AngularPortlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the method should be used as a resource action method. If the resourceId is set at the
 * request the corresponding resource method will be invoked.
 *
 * @see AngularPortlet
 *
 * @author Benjamin Hajnal
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ResourceMethod {

	/**
	 * @return the resourceId of the method
	 */
	public String value();

	/**
	 * @return If the method should be handled as an UploadRequest
	 */
	public boolean multipart() default false;
}
