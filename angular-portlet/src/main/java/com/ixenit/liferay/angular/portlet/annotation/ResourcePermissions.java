package com.ixenit.liferay.angular.portlet.annotation;

import com.ixenit.liferay.angular.portlet.AngularPortlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the method should be called only if the requesting user has
 * any of the permissions from the value list. The permission keys should be the
 * keys from <code>resource-actions/default.xml</code>'s model resource element.
 *
 * @see AngularPortlet
 *
 * @author Benjamin Hajnal
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ResourcePermissions {

	public String[] value() default {};

	public boolean guestOnly() default false;
}
