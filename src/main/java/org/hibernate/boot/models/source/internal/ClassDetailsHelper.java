/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal;

import org.hibernate.boot.models.source.spi.ClassDetails;

/**
 * @author Steve Ebersole
 */
public class ClassDetailsHelper {
	public static boolean isImplementor(Class<?> checkType, ClassDetails classDetails) {
		if ( classDetails.getClassName().equals( checkType.getName() ) ) {
			return true;
		}

		if ( classDetails.getSuperType() != null  ) {
			if ( classDetails.getSuperType().isImplementor( checkType ) ) {
				return true;
			}
		}

		for ( int i = 0; i < classDetails.getImplementedInterfaceTypes().size(); i++ ) {
			final ClassDetails interfaceDetails = classDetails.getImplementedInterfaceTypes().get( i );
			if ( interfaceDetails.isImplementor( checkType ) ) {
				return true;
			}
		}

		return false;
	}

	public static boolean isImplementor(ClassDetails checkType, ClassDetails classDetails) {
		if ( classDetails.getClassName().equals( checkType.getClassName() ) ) {
			return true;
		}

		if ( classDetails.getSuperType() != null  ) {
			if ( classDetails.getSuperType().isImplementor( checkType ) ) {
				return true;
			}
		}

		for ( int i = 0; i < classDetails.getImplementedInterfaceTypes().size(); i++ ) {
			final ClassDetails interfaceDetails = classDetails.getImplementedInterfaceTypes().get( i );
			if ( interfaceDetails.isImplementor( checkType ) ) {
				return true;
			}
		}

		return false;
	}
}
