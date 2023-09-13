/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.source.internal.reflection;

import java.lang.reflect.Field;

import org.hibernate.boot.models.source.internal.LazyAnnotationTarget;
import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.source.spi.FieldDetails;
import org.hibernate.boot.models.spi.ModelProcessingContext;

import static org.hibernate.boot.models.source.internal.ModifierUtils.isPersistableField;


/**
 * @author Steve Ebersole
 */
public class FieldDetailsImpl extends LazyAnnotationTarget implements FieldDetails {
	private final Field field;
	private final ClassDetails type;

	public FieldDetailsImpl(Field field, ModelProcessingContext processingContext) {
		super( field::getAnnotations, processingContext );
		this.field = field;
		this.type = processingContext.getClassDetailsRegistry().resolveClassDetails(
				field.getType().getName(),
				() -> ClassDetailsBuilderImpl.buildClassDetails( field.getType(), getProcessingContext() )
		);
	}

	@Override
	public String getName() {
		return field.getName();
	}

	@Override
	public ClassDetails getType() {
		return type;
	}

	@Override
	public boolean isPersistable() {
		return isPersistableField( field.getModifiers() );
	}
}
