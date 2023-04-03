/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal;

import java.lang.annotation.Annotation;

import org.hibernate.boot.models.source.spi.AnnotationDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationTarget;
import org.hibernate.boot.models.source.spi.AnnotationUsage;
import org.hibernate.boot.models.spi.ModelProcessingContext;

/**
 * ValueNormalizer for handling {@link Annotation} values
 *
 * @author Steve Ebersole
 */
public class AnnotationNormalizer<A extends Annotation> implements ValueNormalizer<A, AnnotationUsage<A>> {
	/**
	 * Singleton access
	 */
	@SuppressWarnings("rawtypes")
	public static final AnnotationNormalizer INSTANCE = new AnnotationNormalizer();

	public static <A extends Annotation> ValueNormalizer<A, AnnotationUsage<A>> singleton() {
		//noinspection unchecked
		return INSTANCE;
	}

	@Override
	public AnnotationUsage<A> normalize(
			A incomingValue,
			AnnotationTarget target,
			ModelProcessingContext processingContext) {
		if ( incomingValue == null ) {
			return null;
		}

		//noinspection unchecked
		final AnnotationDescriptor<A> descriptor = (AnnotationDescriptor<A>) processingContext
				.getAnnotationDescriptorRegistry()
				.getDescriptor( incomingValue.annotationType() );
		return new AnnotationUsageImpl<>( incomingValue, descriptor, target, processingContext );
	}
}
