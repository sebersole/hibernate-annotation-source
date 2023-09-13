/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.intermediate.spi;

/**
 * Models information about a column
 *
 * @author Steve Ebersole
 */
public interface ColumnMetadata extends RelationalValueMetadata {
	default String getName() {
		return getColumnName();
	}

	String getColumnName();

	String getTable();

	Boolean getUnique();

	Boolean getNullable();

	Boolean getInsertable();

	Boolean getUpdatable();

	Integer getLength();

	Integer getPrecision();

	Integer getScale();

	String getColumnDefinition();
}
