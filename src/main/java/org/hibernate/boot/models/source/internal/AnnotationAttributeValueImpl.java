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
public class AnnotationAttributeValueImpl<V,W> implements AnnotationAttributeValue<V,W> {
	private final AnnotationAttributeDescriptor<?,V,W> attributeDescriptor;
	private final W value;

	public AnnotationAttributeValueImpl(AnnotationAttributeDescriptor<?,V,W> attributeDescriptor, W value) {
		this.attributeDescriptor = attributeDescriptor;
		this.value = value;
	}

	@Override
	public AnnotationAttributeDescriptor<?,V,W> getAttributeDescriptor() {
		return attributeDescriptor;
	}

	@Override
	public W getValue() {
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
		return value == null || Objects.equals( value, attributeDescriptor.getAttributeDefault() );
	}
}
