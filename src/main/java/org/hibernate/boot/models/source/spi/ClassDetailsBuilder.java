/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.source.spi;

import org.hibernate.boot.models.spi.ModelProcessingContext;

/**
 * Builder for {@link ClassDetails} references.
 *
 * @author Steve Ebersole
 */
@FunctionalInterface
public interface ClassDetailsBuilder {
	/**
	 * Build a ClassDetails descriptor for a class with the given name
	 */
	ClassDetails buildClassDetails(String name, ModelProcessingContext processingContext);
}
