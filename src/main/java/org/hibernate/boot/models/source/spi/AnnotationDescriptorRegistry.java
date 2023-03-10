/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.spi;

import java.lang.annotation.Annotation;

/**
 * Registry of {@linkplain AnnotationDescriptor descriptors} for all known annotations
 *
 * @author Steve Ebersole
 */
public interface AnnotationDescriptorRegistry {
	/**
	 * Get the descriptor for the given annotation {@code type}
	 */
	<A extends Annotation> AnnotationDescriptor<A> getDescriptor(Class<A> javaType);

	/**
	 * Assuming the {@code descriptor} is a {@linkplain AnnotationDescriptor#getRepeatableContainer() repeatable container},
	 * return the descriptor of the annotation for which it acts as a container.
	 */
	<A extends Annotation> AnnotationDescriptor<A> getContainedRepeatableDescriptor(AnnotationDescriptor<A> descriptor);

	/**
	 * Shorthand for {@code getRepeatableDescriptor( getDescriptor( javaType ) )}
	 */
	<A extends Annotation> AnnotationDescriptor<A> getContainedRepeatableDescriptor(Class<A> javaType);
}
