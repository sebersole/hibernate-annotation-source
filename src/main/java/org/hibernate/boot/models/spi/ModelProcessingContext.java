/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.spi;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.boot.models.source.spi.AnnotationDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationDescriptorRegistry;
import org.hibernate.boot.models.source.spi.AnnotationUsage;
import org.hibernate.boot.models.source.spi.ClassDetailsRegistry;
import org.hibernate.boot.spi.ClassLoaderAccess;
import org.hibernate.boot.spi.MetadataBuildingContext;

/**
 * Context object used while building references for {@link AnnotationDescriptor},
 * {@link AnnotationUsage} and friends
 *
 * @author Steve Ebersole
 */
public interface ModelProcessingContext {
	/**
	 * The registry of annotation descriptors
	 */
	AnnotationDescriptorRegistry getAnnotationDescriptorRegistry();

	/**
	 * Registry of managed-classes
	 */
	ClassDetailsRegistry getClassDetailsRegistry();

	/**
	 * If model processing code needs to load things from the class-loader, they should
	 * really use this access.  At this level, accessing the class-loader at all
	 * sh
	 */
	ClassLoaderAccess getClassLoaderAccess();

	/**
	 * Access larger boostrap context references
	 */
	MetadataBuildingContext getMetadataBuildingContext();

	void registerUsage(AnnotationUsage<?> usage);

	<A extends Annotation> List<AnnotationUsage<A>> getAllUsages(AnnotationDescriptor<A> annotationDescriptor);

	<A extends Annotation> void forEachUsage(AnnotationDescriptor<A> annotationDescriptor, Consumer<AnnotationUsage<A>> consumer);
}
