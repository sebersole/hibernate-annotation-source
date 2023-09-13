/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.source.internal.hcann;

import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.models.source.internal.LazyAnnotationTarget;
import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.source.spi.FieldDetails;
import org.hibernate.boot.models.spi.ModelProcessingContext;

import static org.hibernate.boot.models.source.internal.ModifierUtils.isPersistableField;


/**
 * @author Steve Ebersole
 */
public class FieldDetailsImpl extends LazyAnnotationTarget implements FieldDetails {
	private final XProperty xProperty;
	private final ClassDetails type;

	public FieldDetailsImpl(XProperty xProperty, ModelProcessingContext processingContext) {
		super( xProperty::getAnnotations, processingContext );
		this.xProperty = xProperty;
		this.type = processingContext.getClassDetailsRegistry().resolveClassDetails(
				xProperty.getType().getName(),
				() -> new ClassDetailsImpl( xProperty.getType(), processingContext )
		);
	}

	@Override
	public String getName() {
		return xProperty.getName();
	}

	@Override
	public ClassDetails getType() {
		return type;
	}

	@Override
	public boolean isPersistable() {
		return isPersistableField( xProperty.getModifiers() );
	}
}
