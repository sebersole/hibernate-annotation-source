/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.hibernate.boot.models.source.spi.AnnotationDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationTarget;
import org.hibernate.boot.models.source.spi.AnnotationUsage;
import org.hibernate.boot.models.spi.ModelProcessingContext;

/**
 * Basic support for AnnotationTarget.
 *
 * @implNote Immediately resolves all declared annotations
 *
 * @author Steve Ebersole
 */
public abstract class AbstractAnnotationTarget implements AnnotationTarget {
	private final Map<Class<? extends Annotation>, AnnotationUsage<?>> usageMap = new ConcurrentHashMap<>();

	public AbstractAnnotationTarget(
			Annotation[] annotations,
			ModelProcessingContext processingContext) {
		AnnotationUsageBuilder.processAnnotations( annotations, this, usageMap::put, processingContext );
	}

	@Override
	public <A extends Annotation> AnnotationUsage<A> getAnnotation(AnnotationDescriptor<A> type) {
		return AnnotationWrapperHelper.getAnnotation( type, usageMap );
	}

	@Override
	public <A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotations(AnnotationDescriptor<A> type) {
		return AnnotationWrapperHelper.getRepeatedAnnotations( type, usageMap );
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
		return AnnotationWrapperHelper.getNamedAnnotation( type, matchValue, attributeToMatch, usageMap );
	}
}
