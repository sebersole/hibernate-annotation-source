/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.intermediate.spi;


import org.hibernate.boot.models.intermediate.internal.AbstractOverrideDefinition;
import org.hibernate.boot.models.source.spi.AnnotationDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationUsage;
import org.hibernate.boot.models.source.spi.JpaAnnotations;
import org.hibernate.boot.models.spi.ModelProcessingContext;

import jakarta.persistence.AssociationOverride;

/**
 * @author Steve Ebersole
 */
public class AssociationOverrideMetadata extends AbstractOverrideDefinition {
	public AssociationOverrideMetadata(
			String prefix,
			AnnotationUsage<AssociationOverride> overrideAnnotation,
			ModelProcessingContext processingContext) {
		super( prefix, overrideAnnotation, processingContext );
	}

	@Override
	protected AnnotationDescriptor<?> getTargetAnnotation() {
		return JpaAnnotations.ASSOCIATION_OVERRIDE;
	}

	@Override
	public void apply(AttributeMetadata persistentAttribute) {
		throw new UnsupportedOperationException( "Not yet implemented" );
	}
}
