package com.ixenit.liferay.angular.portlet.annotation;

import com.ixenit.liferay.angular.portlet.AngularPortlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the return value should be cached by the key's value
 *
 * @see AngularPortlet
 *
 * @author Benjamin Hajnal
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ResourceCache {

	public String key();

}
