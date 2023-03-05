/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.spi;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Describes an annotation type (the Class)
 *
 * @author Steve Ebersole
 */
public interface AnnotationDescriptor<A extends Annotation> extends AnnotationTarget {
	@Override
	default Kind getKind() {
		return Kind.ANNOTATION;
	}

	/**
	 * The annotation type
	 */
	Class<A> getAnnotationType();

	/**
	 * Whether the annotation defined as {@linkplain java.lang.annotation.Inherited inherited}
	 */
	boolean isInherited();

	/**
	 * Descriptors for the attributes of the annotation
	 */
	List<AnnotationAttributeDescriptor<A,?,?>> getAttributes();

	/**
	 * Get the attribute descriptor for the named attribute
	 */
	<V,W> AnnotationAttributeDescriptor<A,V,W> getAttribute(String name);

	/**
	 * If the described annotation is repeatable, returns the descriptor
	 * for the annotation which is the repeatable container
	 */
	AnnotationDescriptor<?> getRepeatableContainer();
}
