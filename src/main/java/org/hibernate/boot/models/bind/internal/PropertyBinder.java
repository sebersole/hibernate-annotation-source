/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.bind.internal;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

import org.hibernate.MappingException;
import org.hibernate.boot.models.intermediate.spi.AttributeMetadata;
import org.hibernate.boot.models.intermediate.spi.ManagedTypeMetadata;
import org.hibernate.boot.models.source.internal.AnnotationWrapperHelper;
import org.hibernate.boot.models.source.spi.AnnotationUsage;
import org.hibernate.boot.models.source.spi.JpaAnnotations;
import org.hibernate.boot.models.spi.ModelProcessingContext;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;

import jakarta.persistence.Column;

import static org.hibernate.boot.models.source.internal.AnnotationWrapperHelper.extractValue;

/**
 * Responsible for building {@link Property} references
 *
 * @author Steve Ebersole
 */
public class PropertyBinder {
	public static Property buildProperty(
			AttributeMetadata attributeMetadata,
			ManagedTypeMetadata declaringTypeMetadata,
			Supplier<Table> implicitTableAccess,
			Function<String,Table> tableLocator) {
		switch ( attributeMetadata.getNature() ) {
			case BASIC: {
				return buildBasicProperty( attributeMetadata, declaringTypeMetadata, implicitTableAccess, tableLocator );
			}
			case EMBEDDED: {
				return buildEmbeddedProperty( attributeMetadata, declaringTypeMetadata );
			}
			case ANY: {
				return buildAnyProperty( attributeMetadata, declaringTypeMetadata );
			}
			case TO_ONE: {
				return buildToOneProperty( attributeMetadata, declaringTypeMetadata );
			}
			case PLURAL: {
				return buildPluralProperty( attributeMetadata, declaringTypeMetadata );
			}
		}

		throw new MappingException(
				String.format(
						Locale.ROOT,
						"Unknown attribute nature for `%s.%s` : %s",
						declaringTypeMetadata.getManagedClass().getName(),
						attributeMetadata.getName(),
						attributeMetadata.getNature()
				)
		);
	}

	private static Property buildBasicProperty(
			AttributeMetadata attributeMetadata,
			ManagedTypeMetadata declaringTypeMetadata,
			Supplier<Table> implicitTableAccess,
			Function<String, Table> tableLocator) {
		final ModelProcessingContext processingContext = declaringTypeMetadata.getModelBuildingContext().getModelProcessingContext();
		final Property property = new Property();
		property.setName( attributeMetadata.getName() );

		final AnnotationUsage<Column> columnAnnotation = attributeMetadata.getMember().getAnnotation( JpaAnnotations.COLUMN );
		final Table table = determineColumnTable( columnAnnotation, implicitTableAccess, tableLocator, processingContext );

		final BasicValue valueMapping = new BasicValue( processingContext.getMetadataBuildingContext() );
		valueMapping.setTable( table );

		valueMapping.addColumn( ColumnBinder.bindColumn(
				columnAnnotation,
				() -> implicitColumnName( attributeMetadata, declaringTypeMetadata, processingContext ),
				processingContext
		) );

		property.setValue( valueMapping );
		property.setInsertable( AnnotationWrapperHelper.extractValue( columnAnnotation,"insertable", true ) );
		property.setUpdateable( AnnotationWrapperHelper.extractValue( columnAnnotation, "updatable", true ) );

		return property;
	}

	private static String implicitColumnName(
			AttributeMetadata attributeMetadata,
			ManagedTypeMetadata declaringTypeMetadata,
			ModelProcessingContext processingContext) {
		// todo (annotation-source) : consider moving `#getAttributeRoleBase` and `#getAttributePathBase` up
		//  	to ManagedTypeMetadata for use here with ImplicitBasicColumnNameSource
		// for now, just cheat

		return attributeMetadata.getName();
	}

	private static Table determineColumnTable(
			AnnotationUsage<Column> columnAnnotation,
			Supplier<Table> implicitTableAccess,
			Function<String,Table> tableLocator,
			ModelProcessingContext processingContext) {
		final String tableName = columnAnnotation == null
				? null
				: columnAnnotation.extractAttributeValue( "table" );
		if ( tableName == null ) {
			return implicitTableAccess.get();
		}

		return tableLocator.apply( tableName );
	}

	private static Property buildEmbeddedProperty(
			AttributeMetadata attributeMetadata,
			ManagedTypeMetadata managedTypeMetadata) {
		return null;
	}

	private static Property buildAnyProperty(
			AttributeMetadata attributeMetadata,
			ManagedTypeMetadata managedTypeMetadata) {
		return null;
	}

	private static Property buildToOneProperty(
			AttributeMetadata attributeMetadata,
			ManagedTypeMetadata managedTypeMetadata) {
		return null;
	}

	private static Property buildPluralProperty(
			AttributeMetadata attributeMetadata,
			ManagedTypeMetadata managedTypeMetadata) {
		return null;
	}
}
