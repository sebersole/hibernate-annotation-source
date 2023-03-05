/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.bind.internal;

import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.boot.model.CustomSql;
import org.hibernate.boot.models.source.spi.AnnotationAttributeValue;
import org.hibernate.boot.models.source.spi.AnnotationUsage;
import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;

import static org.hibernate.boot.models.source.spi.AnnotationAttributeDescriptor.VALUE;

/**
 * @author Steve Ebersole
 */
public final class BindingHelper {
	/**
	 * Build a CustomSql reference from {@link org.hibernate.annotations.SQLInsert},
	 * {@link org.hibernate.annotations.SQLUpdate}, {@link org.hibernate.annotations.SQLDelete}
	 * or {@link org.hibernate.annotations.SQLDeleteAll} annotations
	 */
	public static CustomSql extractCustomSql(AnnotationUsage<?> customSqlAnnotation) {
		if ( customSqlAnnotation == null ) {
			return null;
		}

		final String sql = customSqlAnnotation.getAttributeValue( "sql" ).asString();
		final boolean isCallable = customSqlAnnotation.extractAttributeValue( VALUE, false );

		final AnnotationAttributeValue<ResultCheckStyle,ResultCheckStyle> checkValue = customSqlAnnotation.getAttributeValue( "check" );
		final ExecuteUpdateResultCheckStyle checkStyle;
		if ( checkValue == null ) {
			checkStyle = isCallable
					? ExecuteUpdateResultCheckStyle.NONE
					: ExecuteUpdateResultCheckStyle.COUNT;
		}
		else {
			checkStyle = ExecuteUpdateResultCheckStyle.fromResultCheckStyle( checkValue.getValue() );
		}

		return new CustomSql( sql, isCallable, checkStyle );
	}


	private BindingHelper() {
	}
}
