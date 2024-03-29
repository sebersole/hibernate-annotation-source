/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal;

import org.hibernate.boot.models.source.internal.reflection.ClassDetailsImpl;
import org.hibernate.boot.models.source.spi.AnnotationTarget;
import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.source.spi.ClassDetailsRegistry;
import org.hibernate.boot.models.spi.ModelProcessingContext;

/**
 * ValueNormalizer for Class values normalizing to {@link ClassDetails} values
 *
 * @author Steve Ebersole
 */
public class ClassNormalizer implements ValueNormalizer<Class<?>, ClassDetails> {
	private final Class<?> attributeJavaType;

	public ClassNormalizer(Class<?> attributeJavaType) {
		this.attributeJavaType = attributeJavaType;
	}

	@Override
	public ClassDetails normalize(Class<?> incomingValue, AnnotationTarget target, ModelProcessingContext processingContext) {
		if ( incomingValue == null || attributeJavaType.equals( incomingValue ) || void.class.equals( incomingValue ) ) {
			return null;
		}

		final ClassDetailsRegistry classDetailsRegistry = processingContext.getClassDetailsRegistry();
		final ClassDetails existing = classDetailsRegistry.findClassDetails( incomingValue.getName() );
		if ( existing != null ) {
			return existing;
		}
		else {
			final ClassDetails classDetails = new ClassDetailsImpl( incomingValue, processingContext );
			classDetailsRegistry.addClassDetails( classDetails );
			return classDetails;
		}
	}
}
