/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.spi;

/**
 * A processing context that is local to a {@linkplain #getScope() scope}
 *
 * @author Steve Ebersole
 */
public interface LocalProcessingContext<S> {
	/**
	 * The scope of this local context
	 */
	S getScope();

	/**
	 * Access to the top-level processing context
	 */
	ModelProcessingContext getModelProcessingContext();
}
