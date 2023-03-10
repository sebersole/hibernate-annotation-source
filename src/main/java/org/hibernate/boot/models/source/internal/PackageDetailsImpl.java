/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
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
