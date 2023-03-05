/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
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
