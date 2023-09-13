/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.source;

import org.hibernate.HibernateException;

/**
 * Indicates a problem accessing annotation details from the domain model sources
 *
 * @author Steve Ebersole
 */
public class AnnotationAccessException extends HibernateException {
	public AnnotationAccessException(String message) {
		super( message );
	}

	public AnnotationAccessException(String message, Throwable cause) {
		super( message, cause );
	}
}
