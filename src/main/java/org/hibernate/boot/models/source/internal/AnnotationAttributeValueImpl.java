/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal;

import java.util.Objects;

import org.hibernate.boot.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationAttributeValue;

/**
 * @author Steve Ebersole
 */
public class AnnotationAttributeValueImpl<T> implements AnnotationAttributeValue<T> {
	private final AnnotationAttributeDescriptor<?,T> attributeDescriptor;
	private final T value;

	public AnnotationAttributeValueImpl(AnnotationAttributeDescriptor<?,T> attributeDescriptor, T value) {
		this.attributeDescriptor = attributeDescriptor;
		this.value = value;
	}

	@Override
	public AnnotationAttributeDescriptor<?, T> getAttributeDescriptor() {
		return attributeDescriptor;
	}

	@Override
	public T getValue() {
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> X getValue(Class<X> type) {
		// todo (annotation-source) : possibly add some simple conversions.

		// todo (annotation-source) : or possibly typed AnnotationAttributeDescriptor impls (`IntAttributeDescriptor`, ...)
		//		which can be a factory for AnnotationAttributeValue refs based on the descriptor type.

		return (X) getValue();
	}

	@Override
	public boolean isDefaultValue() {
		return Objects.equals( value, attributeDescriptor.getAttributeDefault() );
	}
}
