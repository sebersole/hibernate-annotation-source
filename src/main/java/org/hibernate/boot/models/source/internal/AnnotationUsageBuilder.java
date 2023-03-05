/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.hibernate.boot.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationAttributeValue;
import org.hibernate.boot.models.source.spi.AnnotationDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationDescriptorRegistry;
import org.hibernate.boot.models.source.spi.AnnotationTarget;
import org.hibernate.boot.models.source.spi.AnnotationUsage;
import org.hibernate.boot.models.spi.ModelProcessingContext;
import org.hibernate.internal.util.collections.CollectionHelper;

import static org.hibernate.boot.models.source.internal.AnnotationHelper.extractRawAttributeValue;

/**
 * Helper for building {@link org.hibernate.boot.models.source.spi.AnnotationUsage} instances
 *
 * @author Steve Ebersole
 */
public class AnnotationUsageBuilder {
	/**
	 * Process annotations creating usage instances passed back to the consumer
	 */
	public static void processAnnotations(
			Annotation[] annotations,
			AnnotationTarget target,
			BiConsumer<Class<? extends Annotation>, AnnotationUsage<?>> consumer,
			ModelProcessingContext processingContext) {
		final AnnotationDescriptorRegistry annotationDescriptorRegistry = processingContext.getAnnotationDescriptorRegistry();

		for ( int i = 0; i < annotations.length; i++ ) {
			final Annotation annotation = annotations[ i ];
			final Class<? extends Annotation> annotationType = annotation.annotationType();

			// skip a few well-know ones that are irrelevant
			if ( annotationType == Repeatable.class
					|| annotationType == Target.class
					|| annotationType == Retention.class
					|| annotationType == Documented.class ) {
				continue;
			}

			final AnnotationDescriptor<?> annotationDescriptor = annotationDescriptorRegistry.getDescriptor( annotationType );
			final AnnotationUsage<?> usage = makeUsage(
					annotation,
					annotationDescriptor,
					target,
					processingContext
			);
			consumer.accept( annotationType, usage );
		}
	}

	private static AnnotationUsage<?> makeUsage(
			Annotation annotation,
			AnnotationDescriptor<?> annotationDescriptor,
			AnnotationTarget target,
			ModelProcessingContext processingContext) {
		//noinspection unchecked,rawtypes
		return new AnnotationUsageImpl( annotation, annotationDescriptor, target, processingContext );
	}

	/**
	 * Extracts values from an annotation creating AnnotationAttributeValue references.
	 */
	public static <A extends Annotation> Map<String, AnnotationAttributeValue<?,?>> extractAttributeValues(
			A annotation,
			AnnotationDescriptor<A> annotationDescriptor,
			AnnotationTarget target,
			ModelProcessingContext processingContext) {
		if ( CollectionHelper.isEmpty( annotationDescriptor.getAttributes() ) ) {
			return Collections.emptyMap();
		}

		if ( annotationDescriptor.getAttributes().size() == 1 ) {
			final AnnotationAttributeDescriptor<A,?,?> attributeDescriptor = annotationDescriptor.getAttributes().get( 0 );
			final AnnotationAttributeValue<?, ?> valueWrapper = attributeDescriptor.makeValueWrapper(
					extractRawAttributeValue( annotation, attributeDescriptor.getAttributeName() ),
					target,
					processingContext
			);
			return Collections.singletonMap( attributeDescriptor.getAttributeName(), valueWrapper );
		}

		final Map<String, AnnotationAttributeValue<?,?>> valueMap = new HashMap<>();
		for ( int i = 0; i < annotationDescriptor.getAttributes().size(); i++ ) {
			final AnnotationAttributeDescriptor<A,?,?> attributeDescriptor = annotationDescriptor.getAttributes().get( i );
			final AnnotationAttributeValue<?, ?> valueWrapper = attributeDescriptor.makeValueWrapper(
					extractRawAttributeValue( annotation, attributeDescriptor.getAttributeName() ),
					target,
					processingContext
			);
			valueMap.put( attributeDescriptor.getAttributeName(), valueWrapper );
		}
		return valueMap;
	}

	private AnnotationUsageBuilder() {
		// disallow direct instantiation
	}
}
