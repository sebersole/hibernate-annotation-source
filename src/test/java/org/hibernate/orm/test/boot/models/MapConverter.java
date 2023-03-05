/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.orm.test.boot.models;

import java.util.Map;

import jakarta.persistence.AttributeConverter;

/**
 * @author Steve Ebersole
 */
public class MapConverter implements AttributeConverter<String, Map<String, String>> {
	@Override
	public Map<String, String> convertToDatabaseColumn(String attribute) {
		return null;
	}

	@Override
	public String convertToEntityAttribute(Map<String, String> dbData) {
		return null;
	}
}
