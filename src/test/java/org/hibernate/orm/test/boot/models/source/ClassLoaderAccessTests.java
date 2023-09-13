/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.orm.test.boot.models.source;

import org.hibernate.boot.models.source.internal.hcann.ClassDetailsImpl;
import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.source.spi.ClassDetailsRegistry;
import org.hibernate.boot.models.spi.ModelProcessingContext;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.orm.test.boot.models.ModelHelper;
import org.hibernate.orm.test.util.ClassLoaderAccessImpl;
import org.hibernate.orm.test.util.CollectingClassLoaderService;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Assert expectations about {@link Class} references and how they got loaded
 *
 * @author Steve Ebersole
 */
public class ClassLoaderAccessTests {
	public static final String ENTITY_CLASS_NAME = "org.hibernate.orm.test.boot.models.source.TestEntity";

	@Test
	public void testClassLoaderIsolation() {
		final CollectingClassLoaderService classLoaderService = new CollectingClassLoaderService();
		final BootstrapServiceRegistry bootstrapRegistry = new BootstrapServiceRegistryBuilder()
				.applyClassLoaderService( classLoaderService )
				.build();
		final StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder( bootstrapRegistry ).build();

		final ModelProcessingContext processingContext = ModelHelper.buildProcessingContext( serviceRegistry );
		final ClassDetailsRegistry classDetailsRegistry = processingContext.getClassDetailsRegistry();

		final ClassDetails classDetails = classDetailsRegistry.resolveClassDetails( ENTITY_CLASS_NAME );
		// assert it was handled via the HCANN builder
		assertThat( classDetails ).isInstanceOf( ClassDetailsImpl.class );

		// Currently ORM completely ignores the temp classloader. That manifests here as the entity class being loaded on the "live" class-loader
		assertThat( classLoaderService.getCachedClassForName( ENTITY_CLASS_NAME ) ).isNotNull();
	}

	@Test
	public void testClassLoaderIsolation2() {
		// Unlike `#testClassLoaderIsolation`, here we use a ClassLoaderAccess which incorporates the temp ClassLoader.

		final CollectingClassLoaderService classLoaderService = new CollectingClassLoaderService();
		final BootstrapServiceRegistry bootstrapRegistry = new BootstrapServiceRegistryBuilder()
				.applyClassLoaderService( classLoaderService )
				.build();
		final StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder( bootstrapRegistry ).build();

		final ClassLoader tmpClassLoader = new ClassLoader() {};
		final ClassLoaderAccessImpl classLoaderAccess = new ClassLoaderAccessImpl( tmpClassLoader, classLoaderService );

		final ModelProcessingContext processingContext = ModelHelper.buildProcessingContext( serviceRegistry, classLoaderAccess );
		final ClassDetailsRegistry classDetailsRegistry = processingContext.getClassDetailsRegistry();

		final ClassDetails classDetails = classDetailsRegistry.resolveClassDetails( ENTITY_CLASS_NAME );
		// assert it was handled via the HCANN builder
		assertThat( classDetails ).isInstanceOf( ClassDetailsImpl.class );

		// Now, the entity is not loaded into the "live" class-loader
		assertThat( classLoaderService.getCachedClassForName( ENTITY_CLASS_NAME ) ).isNull();
		assertThat( classLoaderAccess.getClassesLoadedFromTempClassLoader() ).containsOnly( ENTITY_CLASS_NAME );
	}

}
