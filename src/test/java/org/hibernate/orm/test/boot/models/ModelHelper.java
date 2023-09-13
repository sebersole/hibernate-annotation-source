/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.boot.models;

import java.util.Set;

import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.boot.internal.ClassLoaderAccessImpl;
import org.hibernate.boot.models.intermediate.internal.EntityHierarchyBuilder;
import org.hibernate.boot.models.intermediate.spi.EntityHierarchy;
import org.hibernate.boot.models.source.internal.ModelProcessingContextImpl;
import org.hibernate.boot.models.source.internal.hcann.ClassDetailsImpl;
import org.hibernate.boot.models.spi.ModelProcessingContext;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.ClassLoaderAccess;

import org.hibernate.testing.boot.ClassLoaderAccessTestingImpl;
import org.hibernate.testing.boot.MetadataBuildingContextTestingImpl;

/**
 * @author Steve Ebersole
 */
public class ModelHelper {
	public static ModelProcessingContext buildProcessingContext(StandardServiceRegistry registry) {
		final MetadataBuildingContextTestingImpl buildingContext = new MetadataBuildingContextTestingImpl( registry );
		return new ModelProcessingContextImpl( buildingContext );
	}

	public static ModelProcessingContext buildProcessingContext(StandardServiceRegistry registry, ClassLoaderAccess classLoaderAccess) {
		final MetadataBuildingContextTestingImpl buildingContext = new MetadataBuildingContextTestingImpl( registry );
		return new ModelProcessingContextImpl( buildingContext, classLoaderAccess );
	}

	public static Set<EntityHierarchy> buildHierarchies(StandardServiceRegistry registry, Class<?>... classes) {
		return buildHierarchies( buildProcessingContext( registry ), classes );
	}

	public static Set<EntityHierarchy> buildHierarchies(ModelProcessingContext processingContext, Class<?>... classes) {
		final JavaReflectionManager hcannReflectionManager = new JavaReflectionManager();

		for ( int i = 0; i < classes.length; i++ ) {
			final XClass xClass = hcannReflectionManager.toXClass( classes[ i ] );
			new ClassDetailsImpl( xClass, processingContext );
		}

		return EntityHierarchyBuilder.createEntityHierarchies( processingContext );
	}
}
