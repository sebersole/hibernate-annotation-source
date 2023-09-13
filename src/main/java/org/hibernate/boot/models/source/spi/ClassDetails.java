/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.source.spi;

import java.util.List;

import org.hibernate.boot.models.source.internal.ClassDetailsHelper;
import org.hibernate.internal.util.IndexedConsumer;

/**
 * Models details about a class.
 * <p/>
 * Additionally, provides an abstraction for dynamic models where there is no
 * physical {@link Class} as well as support for the Hibernate
 * {@linkplain #getName() entity-name} (XML) feature.
 *
 * @see ClassDetailsRegistry
 *
 * @author Steve Ebersole
 */
public interface ClassDetails extends AnnotationTarget {
	/**
	 * The name of the class.
	 * <p/>
	 * Generally this is the same as the {@linkplain #getClassName() class name}.
	 * But in the case of Hibernate's {@code entity-name} feature, this would
	 * be the {@code entity-name}
	 */
	String getName();

	/**
	 * The name of the {@link Class}, or {@code null} for dynamic models.
	 *
	 * @apiNote Will be {@code null} for dynamic models
	 */
	String getClassName();

	@Override
	default Kind getKind() {
		return Kind.CLASS;
	}

	/**
	 * Whether the class should be considered abstract.
	 */
	boolean isAbstract();

	/**
	 * Details for the class that is the super type for this class.
	 */
	ClassDetails getSuperType();

	/**
	 * Details for the interfaces this class implements.
	 */
	List<ClassDetails> getImplementedInterfaceTypes();

	/**
	 * Whether the described class is an implementor of the given {@code checkType}.
	 */
	default boolean isImplementor(Class<?> checkType) {
		return ClassDetailsHelper.isImplementor( checkType, this );
	}

	/**
	 * Whether the described class is an implementor of the given {@code checkType}.
	 */
	default boolean isImplementor(ClassDetails checkType) {
		return ClassDetailsHelper.isImplementor( checkType, this );
	}

	/**
	 * Get the fields for this class
	 */
	List<FieldDetails> getFields();

	/**
	 * Visit each field
	 */
	void forEachField(IndexedConsumer<FieldDetails> consumer);

	/**
	 * Get the methods for this class
	 */
	List<MethodDetails> getMethods();

	/**
	 * Visit each method
	 */
	void forEachMethod(IndexedConsumer<MethodDetails> consumer);

	/**
	 * Know what you are doing before calling this method
	 */
	<X> Class<X> toJavaClass();
}
