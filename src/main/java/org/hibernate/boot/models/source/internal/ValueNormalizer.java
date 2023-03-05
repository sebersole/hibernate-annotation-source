/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal;

import org.hibernate.boot.models.source.spi.AnnotationTarget;
import org.hibernate.boot.models.spi.ModelProcessingContext;

/**
 * Normalizes annotation attribute values.
 * <p/>
 * This is the code that controls value transformations from the "raw" type directly
 * on the annotation to the "wrapper" type used in {@link org.hibernate.boot.models.source.spi.AnnotationAttributeValue}
 *
 * @param <V> The raw type
 * @param <W> The wrapper type
 *
 * @author Steve Ebersole
 */
@FunctionalInterface
public interface ValueNormalizer<V, W> {
	W normalize(V incomingValue, AnnotationTarget target, ModelProcessingContext processingContext);
}
