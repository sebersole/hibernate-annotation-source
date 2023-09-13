/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
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
