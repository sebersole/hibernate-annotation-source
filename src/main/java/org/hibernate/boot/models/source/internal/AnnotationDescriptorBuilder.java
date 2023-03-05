/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.hibernate.Internal;
import org.hibernate.boot.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationDescriptor;
import org.hibernate.boot.models.source.spi.HibernateAnnotations;
import org.hibernate.boot.models.source.spi.JpaAnnotations;

import static org.hibernate.internal.util.collections.CollectionHelper.arrayList;

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
	public static <A extends Annotation> List<AnnotationAttributeDescriptor<A,?,?>> extractAttributeDescriptors(Class<A> javaType) {
		final Method[] attributes = javaType.getDeclaredMethods();
		final List<AnnotationAttributeDescriptor<A,?,?>> attributeDescriptors = arrayList( attributes.length );

		for ( int i = 0; i < attributes.length; i++ ) {
			final Method attribute = attributes[ i ];
			attributeDescriptors.add( createAttributeDescriptor( javaType, attribute ) );
		}

		return attributeDescriptors;
	}

	@SuppressWarnings("unchecked")
	private static <A extends Annotation,V,W> AnnotationAttributeDescriptor<A,V,W> createAttributeDescriptor(
			Class<A> annotationJavaType,
			Method attributeMethod) {
		final Class<V> attributeJavaType = (Class<V>) attributeMethod.getReturnType();
		return new AnnotationAttributeDescriptorImpl<>( attributeMethod, resolveValueNormalizer( attributeJavaType ) );
	}

	@SuppressWarnings("unchecked")
	private static <V, W> ValueNormalizer<V, W> resolveValueNormalizer(Class<V> attributeJavaType) {
		if ( attributeJavaType == String.class ) {
			return (ValueNormalizer<V, W>) StringNormalizer.INSTANCE;
		}

		if ( attributeJavaType == Class.class ) {
			return (ValueNormalizer<V, W>) new ClassNormalizer( attributeJavaType );
		}

		if ( attributeJavaType.isArray() ) {
			return (ValueNormalizer<V, W>) new ArrayNormalizer<>( resolveValueNormalizer( attributeJavaType.getComponentType() ) );
		}

		if ( attributeJavaType.isAnnotation() ) {
			return (ValueNormalizer<V, W>) AnnotationNormalizer.singleton();
		}

		return (ValueNormalizer<V, W>) PassThroughNormalizer.singleton();
	}
}
