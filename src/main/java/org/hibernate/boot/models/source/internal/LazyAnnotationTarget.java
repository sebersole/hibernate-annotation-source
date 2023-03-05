/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.hibernate.boot.models.source.spi.AnnotationDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationTarget;
import org.hibernate.boot.models.source.spi.AnnotationUsage;
import org.hibernate.boot.models.spi.ModelProcessingContext;

/**
 * AnnotationTarget where we know the annotations up front, but
 * want to delay processing them until (unless!) they are needed
 *
 * @author Steve Ebersole
 */
public abstract class LazyAnnotationTarget implements AnnotationTarget {
	private final Supplier<Annotation[]> annotationSupplier;
	private final ModelProcessingContext processingContext;

	private Map<Class<? extends Annotation>, AnnotationUsage<?>> usagesMap;

	public LazyAnnotationTarget(
			Supplier<Annotation[]> annotationSupplier,
			ModelProcessingContext processingContext) {
		this.annotationSupplier = annotationSupplier;
		this.processingContext = processingContext;
	}

	protected ModelProcessingContext getProcessingContext() {
		return processingContext;
	}

	@Override
	public <A extends Annotation> AnnotationUsage<A> getAnnotation(AnnotationDescriptor<A> type) {
		return AnnotationWrapperHelper.getAnnotation( type, resolveUsagesMap() );
	}

	@Override
	public <A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotations(AnnotationDescriptor<A> type) {
		return AnnotationWrapperHelper.getRepeatedAnnotations( type, resolveUsagesMap() );
	}

	private Map<Class<? extends Annotation>, AnnotationUsage<?>> resolveUsagesMap() {
		if ( usagesMap == null ) {
			usagesMap = buildUsagesMap();
		}
		return usagesMap;
	}

	private Map<Class<? extends Annotation>, AnnotationUsage<?>> buildUsagesMap() {
		final Map<Class<? extends Annotation>, AnnotationUsage<?>> result = new HashMap<>();
		AnnotationUsageBuilder.processAnnotations(
				annotationSupplier.get(),
				this,
				result::put,
				processingContext
		);
		return result;
	}

	@Override
	public <A extends Annotation> void forEachAnnotation(AnnotationDescriptor<A> type, Consumer<AnnotationUsage<A>> consumer) {
		final List<AnnotationUsage<A>> annotations = getRepeatedAnnotations( type );
		if ( annotations == null ) {
			return;
		}
		annotations.forEach( consumer );
	}

	@Override
	public <A extends Annotation> AnnotationUsage<A> getNamedAnnotation(
			AnnotationDescriptor<A> type,
			String matchValue,
			String attributeToMatch) {
		return AnnotationWrapperHelper.getNamedAnnotation( type, matchValue, attributeToMatch, resolveUsagesMap() );
	}
}
