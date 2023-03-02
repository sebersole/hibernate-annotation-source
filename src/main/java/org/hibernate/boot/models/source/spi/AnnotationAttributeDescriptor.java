/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.spi;

import java.lang.annotation.Annotation;

/**
 * Describes an attribute of an annotation
 */
public interface AnnotationAttributeDescriptor<A extends Annotation, T> {
	/**
	 * The name of the attribute.
	 */
	String getAttributeName();

	/**
	 * The {@linkplain Class Java type} of the attribute
	 */
	Class<T> getAttributeType();

	/**
	 * The default value for this annotation
	 */
	T getAttributeDefault();

	/**
	 * Extract the value for the described attribute from
	 * an instance of the containing annotation
	 */
	T extractRawValue(A annotation);
}
