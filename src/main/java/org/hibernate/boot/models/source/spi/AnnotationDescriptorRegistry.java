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
