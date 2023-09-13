/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.source.internal;

import org.hibernate.boot.models.source.spi.AnnotationTarget;
import org.hibernate.boot.models.spi.ModelProcessingContext;
import org.hibernate.internal.util.StringHelper;

/**
 * ValueNormalizer for {@link String} values
 *
 * @author Steve Ebersole
 */
public class StringNormalizer implements ValueNormalizer<String, String> {
	/**
	 * Singleton access
	 */
	public static final StringNormalizer INSTANCE = new StringNormalizer();

	@Override
	public String normalize(String incomingValue, AnnotationTarget target, ModelProcessingContext processingContext) {
		return StringHelper.nullIfEmpty( incomingValue );
	}
}
