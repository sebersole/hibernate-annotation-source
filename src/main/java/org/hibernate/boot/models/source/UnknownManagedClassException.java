/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
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
