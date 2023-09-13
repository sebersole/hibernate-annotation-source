/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.source.internal;

import org.hibernate.boot.models.source.spi.PackageDetails;
import org.hibernate.boot.models.spi.ModelProcessingContext;

/**
 * Details about a "managed package".
 *
 * @author Steve Ebersole
 */
public class PackageDetailsImpl extends LazyAnnotationTarget implements PackageDetails {
	private final Class<?> packageInfoClass;

	public PackageDetailsImpl(
			Class<?> packageInfoClass,
			ModelProcessingContext processingContext) {
		super( packageInfoClass::getDeclaredAnnotations, processingContext );
		this.packageInfoClass = packageInfoClass;
	}

	@Override
	public String getName() {
		return packageInfoClass.getPackageName();
	}
}
