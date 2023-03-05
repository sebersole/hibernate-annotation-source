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
 * @author Steve Ebersole
 */
public class PassThroughNormalizer<V> implements ValueNormalizer<V, V> {
	/**
	 * Singleton access
	 */
	@SuppressWarnings("rawtypes")
	public static final PassThroughNormalizer INSTANCE = new PassThroughNormalizer();

	@SuppressWarnings("unchecked")
	public static <V> PassThroughNormalizer<V> singleton() {
		return INSTANCE;
	}

	@Override
	public V normalize(V incomingValue, AnnotationTarget target, ModelProcessingContext processingContext) {
		return incomingValue;
	}
}
