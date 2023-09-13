/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.boot.models.source;

import java.lang.reflect.Field;
import java.util.List;

import org.hibernate.boot.models.source.internal.ModelProcessingContextImpl;
import org.hibernate.orm.test.boot.models.CustomAnnotation;
import org.hibernate.orm.test.boot.models.CustomMetaAnnotation;
import org.hibernate.orm.test.boot.models.SimpleEntity;
import org.hibernate.boot.models.source.internal.AnnotationUsageImpl;
import org.hibernate.boot.models.source.spi.AnnotationDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationDescriptorRegistry;
import org.hibernate.boot.models.source.spi.AnnotationUsage;
import org.hibernate.boot.models.source.spi.JpaAnnotations;

import org.hibernate.testing.boot.MetadataBuildingContextTestingImpl;
import org.hibernate.testing.orm.junit.ServiceRegistry;
import org.hibernate.testing.orm.junit.ServiceRegistryScope;
import org.junit.jupiter.api.Test;

import jakarta.persistence.Column;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
@ServiceRegistry
public class AnnotationUsageSmokeTests {
	@Test
	void testColumn(ServiceRegistryScope scope) throws NoSuchFieldException {
		final MetadataBuildingContextTestingImpl buildingContext = new MetadataBuildingContextTestingImpl( scope.getRegistry() );
		final ModelProcessingContextImpl processingContext = new ModelProcessingContextImpl( buildingContext );

		final Field nameField = SimpleEntity.class.getDeclaredField( "name" );
		final Column nameColumnAnn = nameField.getAnnotation( Column.class );
		final AnnotationUsageImpl<Column> nameColumnUsage = new AnnotationUsageImpl<>( nameColumnAnn, JpaAnnotations.COLUMN, null, processingContext );

		assertThat( nameColumnUsage.getAttributeValue( "name" ).asString() ).isEqualTo( "description" );
		assertThat( nameColumnUsage.getAttributeValue( "table" ).asString() ).isNull();
		assertThat( nameColumnUsage.getAttributeValue( "nullable" ).asBoolean() ).isFalse();
		assertThat( nameColumnUsage.getAttributeValue( "unique" ).asBoolean() ).isTrue();
		assertThat( nameColumnUsage.getAttributeValue( "insertable" ).asBoolean() ).isFalse();
		assertThat( nameColumnUsage.getAttributeValue( "updatable" ).asBoolean() ).isTrue();
	}

	@Test
	void testMetaAnnotation(ServiceRegistryScope scope) {
		final MetadataBuildingContextTestingImpl buildingContext = new MetadataBuildingContextTestingImpl( scope.getRegistry() );
		final ModelProcessingContextImpl processingContext = new ModelProcessingContextImpl( buildingContext );
		final AnnotationDescriptorRegistry descriptorRegistry = processingContext.getAnnotationDescriptorRegistry();

		final AnnotationDescriptor<CustomAnnotation> descriptor = descriptorRegistry.getDescriptor( CustomAnnotation.class );
		final AnnotationDescriptor<CustomMetaAnnotation> metaDescriptor = descriptorRegistry.getDescriptor( CustomMetaAnnotation.class );
		assertThat( descriptor ).isNotNull();
		assertThat( metaDescriptor ).isNotNull();

		final AnnotationUsage<CustomMetaAnnotation> metaUsage = descriptor.getAnnotation( metaDescriptor );
		assertThat( metaUsage ).isNotNull();
	}

	@Test
	void testGlobalUsages(ServiceRegistryScope scope) {
		final MetadataBuildingContextTestingImpl buildingContext = new MetadataBuildingContextTestingImpl( scope.getRegistry() );
		final ModelProcessingContextImpl processingContext = new ModelProcessingContextImpl( buildingContext );
		final AnnotationDescriptorRegistry descriptorRegistry = processingContext.getAnnotationDescriptorRegistry();

		final AnnotationDescriptor<CustomAnnotation> descriptor = descriptorRegistry.getDescriptor( CustomAnnotation.class );
		final List<AnnotationUsage<CustomAnnotation>> allDescriptorUsages = processingContext.getAllUsages( descriptor );

		final AnnotationDescriptor<CustomMetaAnnotation> metaDescriptor = descriptorRegistry.getDescriptor( CustomMetaAnnotation.class );
		final List<AnnotationUsage<CustomAnnotation>> allMetaDescriptorUsages = processingContext.getAllUsages( descriptor );
	}
}
