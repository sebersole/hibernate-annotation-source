/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.intermediate.spi;

/**
 * Commonality between a {@linkplain ColumnMetadata column}
 * and a {@linkplain FormulaMetadata formula}
 *
 * @author Steve Ebersole
 */
public interface RelationalValueMetadata {
	String getName();
}
