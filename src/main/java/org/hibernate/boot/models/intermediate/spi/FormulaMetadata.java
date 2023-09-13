/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.intermediate.spi;

/**
 * @author Steve Ebersole
 */
public interface FormulaMetadata extends RelationalValueMetadata {
	default String getName() {
		return getExpression();
	}

	String getExpression();
}
