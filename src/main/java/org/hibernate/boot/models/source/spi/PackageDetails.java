/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.spi;

/**
 * Descriptor for a Java package, mainly to act as AnnotationTarget.
 * <p/>
 * Effectively a reference to the package's {@code package-info} class, if one
 *
 * @author Steve Ebersole
 */
public interface PackageDetails extends AnnotationTarget {
	@Override
	default Kind getKind() {
		return Kind.PACKAGE;
	}

	String getName();
}
