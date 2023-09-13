/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.source.internal.dynamic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.source.spi.FieldDetails;
import org.hibernate.boot.models.source.spi.MethodDetails;
import org.hibernate.boot.models.spi.ModelProcessingContext;
import org.hibernate.internal.util.IndexedConsumer;

/**
 * ClassDetails implementation for a {@linkplain org.hibernate.metamodel.RepresentationMode#MAP dynamic model} class
 *
 * @author Steve Ebersole
 */
public class ClassDetailsImpl extends AbstractDynamicAnnotationTarget implements ClassDetails {
	private final String name;
	private final String className;
	private final ClassDetails superType;

	private final List<FieldDetailsImpl> fields = new ArrayList<>();
	private final List<MethodDetailsImpl> methods = new ArrayList<>();

	public ClassDetailsImpl(
			String name,
			String className,
			ClassDetailsImpl superType,
			ModelProcessingContext processingContext) {
		super( processingContext );
		this.name = name;
		this.className = className;
		this.superType = superType;

		processingContext.getClassDetailsRegistry().addClassDetails( name, this );
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public boolean isAbstract() {
		return false;
	}

	@Override
	public ClassDetails getSuperType() {
		return superType;
	}

	@Override
	public List<ClassDetails> getImplementedInterfaceTypes() {
		return Collections.emptyList();
	}

	@Override
	public List<FieldDetails> getFields() {
		//noinspection rawtypes,unchecked
		return (List) fields;
	}

	@Override
	public void forEachField(IndexedConsumer<FieldDetails> consumer) {
		//noinspection unchecked,rawtypes
		fields.forEach( (Consumer) consumer );
	}

	@Override
	public List<MethodDetails> getMethods() {
		//noinspection rawtypes,unchecked
		return (List) methods;
	}

	@Override
	public void forEachMethod(IndexedConsumer<MethodDetails> consumer) {
		//noinspection unchecked,rawtypes
		methods.forEach( (Consumer) consumer );
	}

	@Override
	public <X> Class<X> toJavaClass() {
		throw new UnsupportedOperationException();
	}

	public void addField(FieldDetailsImpl field) {
		fields.add( field );
	}

	public void addMethod(MethodDetailsImpl method) {
		methods.add( method );
	}
}
