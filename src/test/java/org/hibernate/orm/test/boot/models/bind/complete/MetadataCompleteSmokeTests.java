/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.boot.models.bind.complete;

import org.hibernate.boot.models.bind.internal.ManagedResourcesProcessor;
import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.model.process.internal.ManagedResourcesImpl;
import org.hibernate.boot.spi.XmlMappingBinderAccess;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.type.spi.TypeConfiguration;

import org.hibernate.testing.boot.MetadataBuildingContextTestingImpl;
import org.hibernate.testing.orm.junit.ServiceRegistry;
import org.hibernate.testing.orm.junit.ServiceRegistryScope;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
@ServiceRegistry
public class MetadataCompleteSmokeTests {
	@Test
	void simpleTest(ServiceRegistryScope scope) {
		final MetadataBuildingContextTestingImpl buildingContext = new MetadataBuildingContextTestingImpl( scope.getRegistry() );
		final XmlMappingBinderAccess xmlMappingBinderAccess = new XmlMappingBinderAccess( scope.getRegistry() );
		final TypeConfiguration typeConfiguration = buildingContext.getBootstrapContext().getTypeConfiguration();
		final InFlightMetadataCollectorImpl metadataCollector = (InFlightMetadataCollectorImpl) buildingContext.getMetadataCollector();

		final ManagedResourcesImpl managedResources = new ManagedResourcesImpl();
		managedResources.addXmlBinding( xmlMappingBinderAccess.bind( "mappings/boot/complete-entity.xml" ) );

		ManagedResourcesProcessor.bindBootModel( managedResources, buildingContext );

		final PersistentClass entityBinding = metadataCollector.getEntityBinding( XmlMappedEntity.class.getName() );
		assertThat( entityBinding ).isNotNull();

		assertThat( entityBinding.getClassName() ).isEqualTo( XmlMappedEntity.class.getName() );
		assertThat( entityBinding.getEntityName() ).isEqualTo( entityBinding.getClassName() );

		// id
// binding ids not yet implemented
//		assertThat( entityBinding.getDeclaredIdentifierProperty() ).isNotNull();

		// name
		assertThat( entityBinding.getPropertyClosureSpan() ).isEqualTo( 1 );
	}
}
