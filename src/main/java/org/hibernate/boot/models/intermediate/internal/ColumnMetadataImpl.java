/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.intermediate.internal;

import java.util.Objects;

import org.hibernate.boot.models.intermediate.spi.ColumnMetadata;
import org.hibernate.boot.models.source.internal.AnnotationWrapperHelper;
import org.hibernate.boot.models.source.spi.AnnotationUsage;

import static org.hibernate.boot.models.source.internal.AnnotationWrapperHelper.extractValue;

/**
 * Information about a column as determined by any number of annotations
 *
 * @see jakarta.persistence.Column
 * @see jakarta.persistence.DiscriminatorColumn
 * @see jakarta.persistence.JoinColumn
 * @see jakarta.persistence.MapKeyColumn
 * @see jakarta.persistence.MapKeyJoinColumn
 * @see jakarta.persistence.OrderColumn
 * @see jakarta.persistence.PrimaryKeyJoinColumn
 *
 * @author Steve Ebersole
 */
public class ColumnMetadataImpl implements ColumnMetadata {
	private String table;
	private String name;

	private Boolean unique;
	private Boolean nullable;
	private Boolean insertable;
	private Boolean updatable;

	private Integer length;
	private Integer precision;
	private Integer scale;
	private String columnDefinition;

	public ColumnMetadataImpl() {
	}

	public ColumnMetadataImpl(AnnotationUsage<?> annotation) {
		this.name = AnnotationWrapperHelper.extractValue( annotation, "name", null );
		this.table = AnnotationWrapperHelper.extractValue( annotation, "table", null );

		this.unique = AnnotationWrapperHelper.extractValue( annotation, "unique", null );
		this.nullable = AnnotationWrapperHelper.extractValue( annotation, "nullable", null );
		this.insertable = AnnotationWrapperHelper.extractValue( annotation, "insertable", null );
		this.updatable = AnnotationWrapperHelper.extractValue( annotation, "updatable", null );

		this.columnDefinition = AnnotationWrapperHelper.extractValue( annotation, "columnDefinition", null );
		this.length = AnnotationWrapperHelper.extractValue( annotation, "length", 255 );
		this.precision = AnnotationWrapperHelper.extractValue( annotation, "precision", null );
		this.scale = AnnotationWrapperHelper.extractValue( annotation, "scale", null );
	}

	@Override
	public String getColumnName() {
		return name;
	}

	public void setColumnName(String name) {
		this.name = name;
	}

	@Override
	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	@Override
	public Boolean getUnique() {
		return unique;
	}

	public void setUnique(Boolean unique) {
		this.unique = unique;
	}

	@Override
	public Boolean getNullable() {
		return nullable;
	}

	public void setNullable(Boolean nullable) {
		this.nullable = nullable;
	}

	@Override
	public Boolean getInsertable() {
		return insertable;
	}

	public void setInsertable(Boolean insertable) {
		this.insertable = insertable;
	}

	@Override
	public Boolean getUpdatable() {
		return updatable;
	}

	public void setUpdatable(Boolean updatable) {
		this.updatable = updatable;
	}

	@Override
	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	@Override
	public Integer getPrecision() {
		return precision;
	}

	public void setPrecision(Integer precision) {
		this.precision = precision;
	}

	@Override
	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	@Override
	public String getColumnDefinition() {
		return columnDefinition;
	}

	public void setColumnDefinition(String columnDefinition) {
		this.columnDefinition = columnDefinition;
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		ColumnMetadataImpl that = (ColumnMetadataImpl) o;
		return Objects.equals( table, that.table ) && Objects.equals( name, that.name );
	}

	@Override
	public int hashCode() {
		return Objects.hash( table, name );
	}
}
