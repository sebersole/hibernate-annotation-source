/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.intermediate.spi;

import java.lang.annotation.Annotation;
import java.util.Collection;

import org.hibernate.boot.models.source.spi.AnnotationDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationUsage;
import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.internal.util.IndexedConsumer;

import jakarta.persistence.AccessType;

/**
 * Intermediate representation of a {@linkplain jakarta.persistence.metamodel.ManagedType managed type}
 *
 * @author Steve Ebersole
 */
public interface ManagedTypeMetadata {
	/**
	 * The underlying managed-class
	 */
	ClassDetails getManagedClass();

	AccessType getAccessType();

	/**
	 * ModelProcessingContext local to this type
	 */
	ModelBuildingContext getModelBuildingContext();

	/**
	 * Get the number of declared attributes
	 */
	int getNumberOfAttributes();

	/**
	 * Get the declared attributes
	 */
	Collection<AttributeMetadata> getAttributes();

	/**
	 * Visit each declared attributes
	 */
	void forEachAttribute(IndexedConsumer<AttributeMetadata> consumer);

	/**
	 * Find the usages of the given annotation type.
	 * <p/>
	 * Similar to {@link ClassDetails#getAnnotation} except here we search supertypes
	 *
	 * @see #getManagedClass()
	 * @see ClassDetails#getAnnotation
	 */
	<A extends Annotation> AnnotationUsage<A> findAnnotation(AnnotationDescriptor<A> type);

//	/**
//	 * Find the usages of the given annotation type.
//	 * <p/>
//	 * Similar to {@link ClassDetails#getAnnotations} except here we search supertypes
//	 */
//	<A extends Annotation> List<AnnotationUsage<A>> findAnnotations(AnnotationDescriptor<A> type);
//
//	/**
//	 * Visit each usage of the given annotation type.
//	 * <p/>
//	 * Similar to {@link ClassDetails#forEachAnnotation} except here we search supertypes
//	 */
//	<A extends Annotation> void forEachAnnotation(AnnotationDescriptor<A> type, Consumer<AnnotationUsage<A>> consumer);
}
