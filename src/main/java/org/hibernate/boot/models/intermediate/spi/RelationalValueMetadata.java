/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
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
