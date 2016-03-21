package com.ixenit.liferay.angular.portlet.annotation;

import com.ixenit.liferay.angular.portlet.AngularPortlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.portlet.ResourceRequest;

/**
 * Indicates that the actual {@link ResourceRequest} or the
 * {@link ResourceRequest} should be injected into the parameter. The injected
 * type is the same as the parameter's type.
 *
 * @see AngularPortlet
 *
 * @author Benjamin Hajnal
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ResourceContext {

}
