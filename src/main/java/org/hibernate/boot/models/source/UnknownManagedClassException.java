/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source;

import org.hibernate.HibernateException;

/**
 * @author Steve Ebersole
 */
public class UnknownManagedClassException extends HibernateException {
	public UnknownManagedClassException(String message) {
		super( message );
	}

	public UnknownManagedClassException(String message, Throwable cause) {
		super( message, cause );
	}
}
