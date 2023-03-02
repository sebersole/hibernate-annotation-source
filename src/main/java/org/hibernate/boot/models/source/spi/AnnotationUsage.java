/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.spi;

import java.lang.annotation.Annotation;

/**
 * Describes the usage of an annotation.  That is, not the
 * {@linkplain AnnotationDescriptor annotation class} itself, but
 * rather a particular usage of the annotation on one of its
 * allowable {@linkplain AnnotationTarget targets}.
 *
 * @apiNote Abstracts the underlying source of the annotation information,
 * whether that is the {@linkplain Annotation annotation} itself, Jandex,
 * HCANN, etc.
 *
 * @author Steve Ebersole
 */
public interface AnnotationUsage<A extends Annotation> {
	/**
	 * Descriptor of the used annotation
	 */
	AnnotationDescriptor<A> getAnnotationDescriptor();

	/**
	 * The target where this usage occurs
	 */
	AnnotationTarget getAnnotationTarget();

	/**
	 * The value of the named annotation attribute
	 */
	<X> AnnotationAttributeValue<X> getAttributeValue(String name);

	/**
	 * The value of the named annotation attribute
	 */
	<X> AnnotationAttributeValue<X> getAttributeValue(AnnotationAttributeDescriptor<A,X> attributeDescriptor);
}
