/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.source.internal.dynamic;


import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.source.spi.FieldDetails;
import org.hibernate.boot.models.spi.ModelProcessingContext;

/**
 * Models a field
 *
 * @author Steve Ebersole
 */
public class FieldDetailsImpl extends AbstractDynamicAnnotationTarget implements DynamicMemberDetails, FieldDetails {
	private final String name;
	private ClassDetails type;

	public FieldDetailsImpl(String name, ModelProcessingContext processingContext) {
		super( processingContext );
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ClassDetails getType() {
		return type;
	}

	public void setType(ClassDetails type) {
		this.type = type;
	}

	@Override
	public boolean isPersistable() {
		return true;
	}
}
