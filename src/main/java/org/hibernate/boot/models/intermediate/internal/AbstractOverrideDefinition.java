/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.intermediate.internal;

import java.util.Objects;

import org.hibernate.AssertionFailure;
import org.hibernate.boot.models.intermediate.spi.AttributeMetadata;
import org.hibernate.boot.models.source.internal.AnnotationWrapperHelper;
import org.hibernate.boot.models.source.spi.AnnotationDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationUsage;
import org.hibernate.boot.models.spi.ModelProcessingContext;
import org.hibernate.internal.util.StringHelper;

/**
 * @author Strong Liu
 */
public abstract class AbstractOverrideDefinition {

	protected static final String PROPERTY_PATH_SEPARATOR = ".";
	protected final String attributePath;
	protected final ModelProcessingContext processingContext;

	private boolean isApplied;

	public AbstractOverrideDefinition(
			String prefix,
			AnnotationUsage<?> overrideAnnotation,
			ModelProcessingContext processingContext) {
		if ( overrideAnnotation == null ) {
			throw new IllegalArgumentException( "AnnotationInstance passed cannot be null" );
		}

		if ( !getTargetAnnotation().equals( overrideAnnotation.getAnnotationDescriptor() ) ) {
			throw new AssertionFailure( "Unexpected annotation passed to the constructor" );
		}

		this.attributePath = createAttributePath(
				prefix,
				AnnotationWrapperHelper.extractValue( overrideAnnotation, "name" )
		);
		this.processingContext = processingContext;
	}

	protected static String createAttributePath(String prefix, String name) {
		if ( StringHelper.isEmpty( name ) ) {
			throw new AssertionFailure( "name attribute in @AttributeOverride can't be empty" );
		}
		String path = "";
		if ( StringHelper.isNotEmpty( prefix ) ) {
			path += prefix;
		}
		if ( StringHelper.isNotEmpty( path ) && !path.endsWith( PROPERTY_PATH_SEPARATOR ) ) {
			path += PROPERTY_PATH_SEPARATOR;
		}
		path += name;
		return path;
	}

	public String getAttributePath(){
		return attributePath;
	}

	public abstract void apply(AttributeMetadata persistentAttribute);

	protected abstract AnnotationDescriptor<?> getTargetAnnotation();

	public boolean isApplied() {
		return isApplied;
	}

	public void setApplied(boolean applied) {
		isApplied = applied;
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( !( o instanceof AbstractOverrideDefinition ) ) {
			return false;
		}

		AbstractOverrideDefinition that = (AbstractOverrideDefinition) o;
		return Objects.deepEquals( this.attributePath, that.attributePath );
	}

	@Override
	public int hashCode() {
		return attributePath != null ? attributePath.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "AbstractOverrideDefinition{attributePath='" + attributePath + "'}";
	}
}
