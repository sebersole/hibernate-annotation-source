/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Internal;
import org.hibernate.boot.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationDescriptor;
import org.hibernate.boot.models.source.spi.HibernateAnnotations;
import org.hibernate.boot.models.source.spi.JpaAnnotations;

/**
 * Builders for AnnotationDescriptor instances.
 *
 * @see HibernateAnnotations
 * @see JpaAnnotations
 *
 * @author Steve Ebersole
 */
public class AnnotationDescriptorBuilder {
	@Internal
	public static <A extends Annotation> AnnotationDescriptor<A> createOrmDescriptor(Class<A> javaType) {
		return createOrmDescriptor( javaType, null );
	}

	@Internal
	public static <A extends Annotation> AnnotationDescriptor<A> createOrmDescriptor(
			Class<A> javaType,
			AnnotationDescriptor<?> repeatableContainer) {
		assert javaType != null;

		return new OrmAnnotationDescriptorImpl<>(
				javaType,
				extractAttributeDescriptors( javaType ),
				repeatableContainer
		);
	}

	@Internal
	public static <A extends Annotation> List<AnnotationAttributeDescriptor<A,?>> extractAttributeDescriptors(Class<A> javaType) {
		final Method[] attributes = javaType.getDeclaredMethods();
		final List<AnnotationAttributeDescriptor<A,?>> attributeDescriptors = new ArrayList<>();

		for ( int i = 0; i < attributes.length; i++ ) {
			attributeDescriptors.add( new AnnotationAttributeDescriptorImpl<>( attributes[i] ) );
		}

		return attributeDescriptors;
	}
}
