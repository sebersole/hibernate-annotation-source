/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal.dynamic;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.hibernate.boot.models.source.AnnotationAccessException;
import org.hibernate.boot.models.source.internal.AnnotationUsageImpl;
import org.hibernate.boot.models.source.internal.AnnotationWrapperHelper;
import org.hibernate.boot.models.source.spi.AnnotationDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationUsage;
import org.hibernate.boot.models.spi.ModelProcessingContext;

import static org.hibernate.boot.models.source.spi.AnnotationAttributeDescriptor.VALUE;

/**
 * Implementation of  AnnotationTarget where the annotations are not known up
 * front; rather, they are {@linkplain  #apply(AnnotationUsage) applied} later
 *
 * @author Steve Ebersole
 */
public abstract class AbstractDynamicAnnotationTarget implements DynamicAnnotationTarget {
	private final ModelProcessingContext processingContext;
	private final Map<Class<? extends Annotation>,AnnotationUsage<?>> usagesMap = new HashMap<>();

	public AbstractDynamicAnnotationTarget(ModelProcessingContext processingContext) {
		this.processingContext = processingContext;
	}

	@Override
	public <X extends Annotation> void apply(List<AnnotationUsage<X>> annotationUsages) {
		// todo (annotation-source) : handle meta-annotations
		annotationUsages.forEach( this::apply );
	}

	/**
	 * Applies the given {@code annotationUsage} to this target.
	 *
	 * @apiNote
	 * todo (annotation-source) : It is undefined currently what happens if the
	 * 		{@link AnnotationUsage#getAnnotationDescriptor() annotation type} is
	 * 		already applied on this target.
	 */
	@Override
	public <X extends Annotation> void apply(AnnotationUsage<X> annotationUsage) {
		final AnnotationDescriptor<?> annotationDescriptor = annotationUsage.getAnnotationDescriptor();
		final Class<? extends Annotation> annotationJavaType = annotationDescriptor.getAnnotationType();

		final AnnotationUsage<?> previous = usagesMap.put( annotationJavaType, annotationUsage );

		if ( previous != null ) {
			// todo (annotation-source) : ignore?  log?  exception?
		}
	}

	@Override
	public void apply(Annotation[] annotations) {
		// todo (annotation-source) : handle meta-annotations
		for ( int i = 0; i < annotations.length; i++ ) {
			apply( annotations[i] );
		}
	}

	@Override
	public void apply(Annotation annotation) {
		//noinspection rawtypes,unchecked
		final AnnotationUsageImpl usage = new AnnotationUsageImpl(
				annotation,
				processingContext.getAnnotationDescriptorRegistry().getDescriptor( annotation.annotationType() ),
				this,
				processingContext
		);
		apply( usage );
	}

	@Override
	public <A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotations(AnnotationDescriptor<A> type) {
		return AnnotationWrapperHelper.getRepeatedAnnotations( type, usagesMap );
	}

	@Override
	public <A extends Annotation> void forEachAnnotation(
			AnnotationDescriptor<A> type,
			Consumer<AnnotationUsage<A>> consumer) {
		if ( type.getRepeatableContainer() != null ) {
			// instead we want to look for the container annotation
			final Class<? extends Annotation> annotationJavaType = type.getRepeatableContainer().getAnnotationType();
			final AnnotationUsage<?> containerUsage = usagesMap.get( annotationJavaType );
			if ( containerUsage != null ) {
				final List<AnnotationUsage<A>> usages = AnnotationWrapperHelper.extractValue( containerUsage, VALUE );
				for ( int i = 0; i < usages.size(); i++ ) {
					consumer.accept( usages.get( i ) );
				}
			}
		}
		else {
			final Class<A> annotationJavaType = type.getAnnotationType();
			//noinspection unchecked
			final AnnotationUsage<A> usage = (AnnotationUsage<A>) usagesMap.get( annotationJavaType );
			if ( usage != null ) {
				consumer.accept( usage );
			}
		}
	}

	@Override
	public <A extends Annotation> AnnotationUsage<A> getAnnotation(AnnotationDescriptor<A> type) {
		//noinspection unchecked
		return (AnnotationUsage<A>) usagesMap.get( type.getAnnotationType() );
	}

	@Override
	public <A extends Annotation> AnnotationUsage<A> getNamedAnnotation(
			AnnotationDescriptor<A> type,
			String matchValue,
			String attributeToMatch) {
		if ( type.getRepeatableContainer() == null ) {
			throw new AnnotationAccessException( "Expecting repeatable annotation" );
		}

		return AnnotationWrapperHelper.getNamedAnnotation( type, matchValue, attributeToMatch, usagesMap );
	}
}
