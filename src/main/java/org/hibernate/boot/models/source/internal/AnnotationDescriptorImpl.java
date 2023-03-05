/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal;

import java.lang.annotation.Annotation;
import java.util.List;

import org.hibernate.boot.models.source.AnnotationAccessException;
import org.hibernate.boot.models.source.internal.reflection.ClassDetailsBuilderImpl;
import org.hibernate.boot.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationTarget;
import org.hibernate.boot.models.source.spi.HibernateAnnotations;
import org.hibernate.boot.models.source.spi.JpaAnnotations;
import org.hibernate.boot.models.spi.ModelProcessingContext;

/**
 * AnnotationDescriptor used for annotations other than {@linkplain JpaAnnotations JPA}
 * and {@linkplain HibernateAnnotations Hibernate} annotations.
 * <p/>
 * Processes the annotations associated with the {@linkplain #getAnnotationType() annotation class}
 * and makes them available via its {@link AnnotationTarget} implementation.
 *
 * @author Steve Ebersole
 */
public class AnnotationDescriptorImpl<A extends Annotation>
		extends AbstractAnnotationTarget
		implements AnnotationDescriptor<A> {
	private final Class<A> annotationType;
	private final AnnotationDescriptor<?> repeatableContainer;

	private final boolean inherited;
	private final List<AnnotationAttributeDescriptor<A,?,?>> attributeDescriptors;

	public AnnotationDescriptorImpl(
			Class<A> annotationType,
			AnnotationDescriptor<?> repeatableContainer,
			ModelProcessingContext processingContext) {
		super( annotationType.getAnnotations(), processingContext );
		this.annotationType = annotationType;
		this.repeatableContainer = repeatableContainer;

		this.inherited = AnnotationHelper.isInherited( annotationType );
		this.attributeDescriptors = AnnotationDescriptorBuilder.extractAttributeDescriptors( annotationType );

		processingContext.getClassDetailsRegistry().resolveManagedClass(
				annotationType.getName(),
				ClassDetailsBuilderImpl::buildClassDetailsStatic
		);
	}

	@Override
	public String getName() {
		return annotationType.getName();
	}

	@Override
	public Class<A> getAnnotationType() {
		return annotationType;
	}

	@Override
	public boolean isInherited() {
		return inherited;
	}

	@Override
	public List<AnnotationAttributeDescriptor<A,?,?>> getAttributes() {
		return attributeDescriptors;
	}

	@Override
	public AnnotationDescriptor<?> getRepeatableContainer() {
		return repeatableContainer;
	}

	@Override
	public <V, W> AnnotationAttributeDescriptor<A, V, W> getAttribute(String name) {
		for ( int i = 0; i < attributeDescriptors.size(); i++ ) {
			final AnnotationAttributeDescriptor<A,?,?> attributeDescriptor = attributeDescriptors.get( i );
			if ( attributeDescriptor.getAttributeName().equals( name ) ) {
				//noinspection unchecked
				return (AnnotationAttributeDescriptor<A,V,W>) attributeDescriptor;
			}
		}
		throw new AnnotationAccessException( "No such attribute : " + annotationType.getName() + "." + name );
	}
}
