/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.orm.test.boot.models;

import java.util.Set;

import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.boot.models.intermediate.internal.EntityHierarchyBuilder;
import org.hibernate.boot.models.intermediate.spi.EntityHierarchy;
import org.hibernate.boot.models.source.internal.ModelProcessingContextImpl;
import org.hibernate.boot.models.source.internal.hcann.ClassDetailsImpl;
import org.hibernate.boot.models.spi.ModelProcessingContext;
import org.hibernate.boot.registry.StandardServiceRegistry;

import org.hibernate.testing.boot.MetadataBuildingContextTestingImpl;

/**
 * @author Steve Ebersole
 */
public class ModelHelper {
	public static ModelProcessingContext buildProcessingContext(StandardServiceRegistry registry) {
		final MetadataBuildingContextTestingImpl buildingContext = new MetadataBuildingContextTestingImpl( registry );
		return new ModelProcessingContextImpl( buildingContext );
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
