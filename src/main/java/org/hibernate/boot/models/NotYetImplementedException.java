/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models;

import org.hibernate.HibernateException;

/**
 * Indicates a method or feature that is currently not (yet) implemented
 *
 * @author Steve Ebersole
 */
public class NotYetImplementedException extends HibernateException {
	public NotYetImplementedException(String message) {
		super( message );
	}
	public NotYetImplementedException(Class<?> nonImplementor) {
		super( "Not yet implemented on " + nonImplementor.getName() );
	}
}
