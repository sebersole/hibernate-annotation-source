/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.spi;

import java.lang.annotation.Annotation;

/**
 * @author Steve Ebersole
 */
public interface AnnotationDescriptorRegistry {
	<A extends Annotation> AnnotationDescriptor<A> getDescriptor(Class<A> javaType);

	<A extends Annotation> AnnotationDescriptor<A> getRepeatableDescriptor(AnnotationDescriptor<?> descriptor);

	<A extends Annotation> AnnotationDescriptor<A> getRepeatableDescriptor(Class<?> javaType);
}
