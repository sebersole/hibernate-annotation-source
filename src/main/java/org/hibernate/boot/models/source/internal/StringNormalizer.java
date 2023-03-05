/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal;

import org.hibernate.boot.models.source.spi.AnnotationTarget;
import org.hibernate.boot.models.spi.ModelProcessingContext;
import org.hibernate.internal.util.StringHelper;

/**
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
