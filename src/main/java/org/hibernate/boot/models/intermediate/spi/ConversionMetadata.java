/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.intermediate.spi;

import org.hibernate.boot.models.source.spi.ClassDetails;

/**
 * Information about an attribute conversion as defined by explicit {@link jakarta.persistence.Convert}
 * and {@link jakarta.persistence.Converts} annotations.
 * <p/>
 * Auto-applied converters are handled separately later.
 *
 * @author Steve Ebersole
 */
public class ConversionMetadata {
	private final boolean conversionEnabled;
	private final ClassDetails converterClass;

	public ConversionMetadata(ClassDetails converterClass, boolean conversionEnabled) {
		this.conversionEnabled = conversionEnabled;
		this.converterClass = converterClass;
	}

	public boolean isConversionEnabled() {
		return conversionEnabled;
	}

	public ClassDetails getConverterClass() {
		return converterClass;
	}
}
