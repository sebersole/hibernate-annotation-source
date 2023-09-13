/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.intermediate.internal;

import org.hibernate.boot.models.source.spi.AnnotationDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationTarget;
import org.hibernate.boot.models.source.spi.AnnotationUsage;
import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.source.spi.MethodDetails;

import static org.hibernate.boot.models.spi.ModelSourceLogging.MODEL_SOURCE_LOGGER;

/**
 * @author Steve Ebersole
 */
public class MetadataHelper {

	/**
	 * Find the name of the method in the class (described by the descriptor) that
	 * is annotated with the given lifecycle callback annotation.
	 *
	 * @param callbackClassInfo The descriptor for the class in which to find
	 * the lifecycle callback method
	 * @param eventAnnotationType The type of lifecycle callback to look for
	 * @param listener Is the {@code callbackClassInfo} a listener, as opposed to
	 * an Entity/MappedSuperclass?  Used here to validate method signatures.
	 *
	 * @return The name of the callback method, or {@code null} indicating none was found
	 */
	public static MethodDetails findCallback(
			ClassDetails callbackClassInfo,
			AnnotationDescriptor<?> eventAnnotationType,
			boolean listener) {
		final Iterable<? extends AnnotationUsage<?>> listenerAnnotations = callbackClassInfo.getRepeatedAnnotations( eventAnnotationType );
		for ( AnnotationUsage<?> listenerAnnotation : listenerAnnotations ) {
			final AnnotationTarget annotationTarget = listenerAnnotation.getAnnotationTarget();
			if ( ! (annotationTarget instanceof MethodDetails ) ) {
				MODEL_SOURCE_LOGGER.debugf(
						"Skipping callback annotation [%s] for class [%s] as it was " +
								"applied to target other than a method : %s",
						eventAnnotationType.getAnnotationType(),
						callbackClassInfo.getName(),
						annotationTarget
				);
				continue;
			}

			final MethodDetails targetMethod = (MethodDetails) annotationTarget;

			// todo (annotation-source) - validate method arguments

			return targetMethod;
		}

		return null;
	}

	private MetadataHelper() {
		// disallow direct instantiation
	}
}
