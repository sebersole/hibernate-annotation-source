/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.bind.internal;

import java.util.function.Supplier;

import org.hibernate.boot.models.source.internal.AnnotationWrapperHelper;
import org.hibernate.boot.models.source.spi.AnnotationUsage;
import org.hibernate.boot.models.spi.ModelProcessingContext;
import org.hibernate.mapping.Column;

import static org.hibernate.boot.models.source.internal.AnnotationWrapperHelper.extractValue;

/**
 * @author Steve Ebersole
 */
public class ColumnBinder {
	public static Column bindColumn(
			AnnotationUsage<?> annotationUsage,
			Supplier<String> defaultNameSupplier,
			ModelProcessingContext processingContext) {
		return bindColumn(
				annotationUsage,
				defaultNameSupplier,
				false,
				true,
				255,
				0,
				0,
				processingContext
		);
	}

	public static Column bindColumn(
			AnnotationUsage<?> annotationUsage,
			Supplier<String> defaultNameSupplier,
			boolean uniqueByDefault,
			boolean nullableByDefault,
			long lengthByDefault,
			int precisionByDefault,
			int scaleByDefault,
			ModelProcessingContext processingContext) {
		final Column result = new Column();
		result.setName( columnName( annotationUsage, defaultNameSupplier, processingContext ) );
		result.setUnique( AnnotationWrapperHelper.extractValue( annotationUsage, "unique", uniqueByDefault ) );
		result.setNullable( AnnotationWrapperHelper.extractValue( annotationUsage, "nullable", nullableByDefault ) );
		result.setSqlType( AnnotationWrapperHelper.extractValue( annotationUsage, "columnDefinition" ) );
		result.setLength( AnnotationWrapperHelper.extractValue( annotationUsage, "length", lengthByDefault ) );
		result.setPrecision( AnnotationWrapperHelper.extractValue( annotationUsage,"precision", precisionByDefault ) );
		result.setScale( AnnotationWrapperHelper.extractValue( annotationUsage, "scale", scaleByDefault ) );
		return result;
	}

	private static String columnName(
			AnnotationUsage<?> columnAnnotation,
			Supplier<String> defaultNameSupplier,
			ModelProcessingContext processingContext) {
		return AnnotationWrapperHelper.extractValue( columnAnnotation, "name", defaultNameSupplier );
	}

	private ColumnBinder() {
	}
}
