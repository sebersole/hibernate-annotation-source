/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.source.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.boot.models.source.spi.AnnotationTarget;
import org.hibernate.boot.models.spi.ModelProcessingContext;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.internal.util.collections.CollectionHelper;

/**
 * ValueNormalizer for handling arrays of values, with an element ValueNormalizer
 *
 * @author Steve Ebersole
 */
public class ArrayNormalizer<I,O> implements ValueNormalizer<I[], List<O>>{
	private final ValueNormalizer<I,O> elementNormalizer;

	public ArrayNormalizer(ValueNormalizer<I, O> elementNormalizer) {
		this.elementNormalizer = elementNormalizer;
	}

	@Override
	public List<O> normalize(I[] incomingValue, AnnotationTarget target, ModelProcessingContext processingContext) {
		if ( ArrayHelper.isEmpty( incomingValue ) ) {
			return Collections.emptyList();
		}

		final ArrayList<O> result = CollectionHelper.arrayList( incomingValue.length );
		for ( int i = 0; i < incomingValue.length; i++ ) {
			result.add( elementNormalizer.normalize( incomingValue[i], target, processingContext ) );
		}
		return result;
	}
}
