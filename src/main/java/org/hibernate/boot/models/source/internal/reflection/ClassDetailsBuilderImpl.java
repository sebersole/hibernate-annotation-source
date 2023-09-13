/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.source.internal.reflection;

import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.source.spi.ClassDetailsBuilder;
import org.hibernate.boot.models.spi.ModelProcessingContext;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;

/**
 * ClassDetailsBuilder implementation based on {@link Class}
 *
 * @author Steve Ebersole
 */
public class ClassDetailsBuilderImpl implements ClassDetailsBuilder {
	/**
	 * Singleton access
	 */
	public static final ClassDetailsBuilderImpl INSTANCE = new ClassDetailsBuilderImpl();

	@Override
	public ClassDetails buildClassDetails(String name, ModelProcessingContext processingContext) {
		return buildClassDetailsStatic( name, processingContext );
	}

	public static ClassDetails buildClassDetailsStatic(String name, ModelProcessingContext processingContext) {
		return buildClassDetails(
				processingContext.getClassLoaderAccess().classForName( name ),
				processingContext
		);
	}

	public static ClassDetails buildClassDetails(Class<?> javaClass, ModelProcessingContext processingContext) {
		return new ClassDetailsImpl( javaClass, processingContext );
	}
}
