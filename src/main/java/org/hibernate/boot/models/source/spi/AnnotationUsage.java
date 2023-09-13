/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.source.spi;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

import org.hibernate.boot.models.source.internal.AnnotationWrapperHelper;

/**
 * Describes the usage of an annotation - not the
 * {@linkplain AnnotationDescriptor annotation class} itself, but
 * rather a particular usage of the annotation on one of its
 * allowable {@linkplain AnnotationTarget targets}.
 *
 * @apiNote Abstracts the underlying source of the annotation information,
 * whether that is the {@linkplain Annotation annotation} itself, JAXB, Jandex,
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
	<V,W> AnnotationAttributeValue<V,W> getAttributeValue(String name);

	/**
	 * The value of the named annotation attribute
	 */
	<V,W> AnnotationAttributeValue<V,W> getAttributeValue(AnnotationAttributeDescriptor<A,V,W> attributeDescriptor);

	default <X> X extractAttributeValue(String name) {
		return AnnotationWrapperHelper.extractValue( this, name );
	}

	default <X> X extractAttributeValue(String name, X fallback) {
		return AnnotationWrapperHelper.extractValue( this, name, fallback );
	}

	default <X> X extractAttributeValue(String name, Supplier<X> fallbackSupplier) {
		return AnnotationWrapperHelper.extractValue( this, name, fallbackSupplier );
	}
}
