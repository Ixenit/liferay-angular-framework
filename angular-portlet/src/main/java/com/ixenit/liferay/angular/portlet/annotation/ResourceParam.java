package com.ixenit.liferay.angular.portlet.annotation;

import com.ixenit.liferay.angular.portlet.AngularPortlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the value for the method parameter should be injected from the same named parameter from the
 * request's parameter map.
 *
 * @see AngularPortlet
 *
 * @author Benjamin Hajnal
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ResourceParam {

	/**
	 * If it is set to true and the parameter is missing an IllegalArgumentException will be thrown before
	 * invoking.
	 *
	 * @return if the parameter is required
	 */
	public boolean required() default true;

	public String dateFormat() default "";

}