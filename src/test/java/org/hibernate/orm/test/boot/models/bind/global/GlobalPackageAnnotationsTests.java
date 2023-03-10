/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.orm.test.boot.models.bind.global;

import java.util.Map;

import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.model.convert.internal.AttributeConverterManager;
import org.hibernate.boot.model.convert.spi.RegisteredConversion;
import org.hibernate.boot.model.process.internal.ManagedResourcesImpl;
import org.hibernate.boot.models.bind.internal.ManagedResourcesProcessor;
import org.hibernate.orm.test.boot.models.MapConverter;
import org.hibernate.orm.test.boot.models.bind.BasicEntity;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
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
public class GlobalPackageAnnotationsTests {
	@Test
	void simpleTest(ServiceRegistryScope scope) {
		final MetadataBuildingContextTestingImpl buildingContext = new MetadataBuildingContextTestingImpl( scope.getRegistry() );
		final ManagedResourcesImpl managedResources = new ManagedResourcesImpl();
		managedResources.addAnnotatedClassReference( BasicEntity.class );
		managedResources.addAnnotatedPackageName( getClass().getPackageName() );

		ManagedResourcesProcessor.bindBootModel( managedResources, buildingContext );

		final TypeConfiguration typeConfiguration = buildingContext.getBootstrapContext().getTypeConfiguration();
		final InFlightMetadataCollectorImpl metadataCollector = (InFlightMetadataCollectorImpl) buildingContext.getMetadataCollector();

		// @JavaTypeRegistration
		final JavaType<?> javaType = typeConfiguration.getJavaTypeRegistry().getDescriptor( String.class );
		assertThat( javaType ).isInstanceOf( CustomStringJavaType.class );

		// @JdbcTypeRegistration
		final JdbcType jdbcType = typeConfiguration.getJdbcTypeRegistry().getDescriptor( SqlTypes.VARCHAR );
		assertThat( jdbcType ).isInstanceOf( CustomVarcharJdbcType.class );

		// @ConverterRegistration
		final AttributeConverterManager converterManager = metadataCollector.getAttributeConverterManager();
		final RegisteredConversion registeredConversion = converterManager.findRegisteredConversion( Map.class );
		assertThat( registeredConversion ).isNotNull();
		assertThat( registeredConversion.getConverterDescriptor().getAttributeConverterClass() ).isEqualTo( MapConverter.class );

	}
}
