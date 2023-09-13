/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.boot.models.bind;

import org.hibernate.boot.model.process.internal.ManagedResourcesImpl;
import org.hibernate.boot.models.bind.internal.ManagedResourcesProcessor;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.orm.test.boot.models.SimpleEntity;

import org.hibernate.testing.boot.MetadataBuildingContextTestingImpl;
import org.hibernate.testing.orm.junit.ServiceRegistry;
import org.hibernate.testing.orm.junit.ServiceRegistryScope;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
@ServiceRegistry
public class ManagedResourcesProcessorSmokeTests {
	@Test
	void simpleTest(ServiceRegistryScope scope) {
		final MetadataBuildingContextTestingImpl buildingContext = new MetadataBuildingContextTestingImpl( scope.getRegistry() );
		final ManagedResourcesImpl managedResources = new ManagedResourcesImpl();
		managedResources.addAnnotatedClassReference( SimpleEntity.class );
		managedResources.addAnnotatedPackageName( getClass().getPackageName() );

		ManagedResourcesProcessor.bindBootModel( managedResources, buildingContext );

		final RootClass entityBinding = (RootClass) buildingContext
				.getMetadataCollector()
				.getEntityBinding( SimpleEntity.class.getName() );
		PropertyBinderSmokeTests.verifySimpleColumnEntityDetails( entityBinding );

		assertThat( entityBinding.getProperties() ).hasSize( 2 );

		for ( Property property : entityBinding.getProperties() ) {
			PropertyBinderSmokeTests.verifySimpleEntityProperty( entityBinding, property );
		}
	}
}
