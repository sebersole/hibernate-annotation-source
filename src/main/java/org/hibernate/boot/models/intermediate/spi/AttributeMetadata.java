/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.intermediate.spi;


import org.hibernate.boot.models.source.spi.MemberDetails;

/**
 * @author Steve Ebersole
 */
public interface AttributeMetadata {
	String getName();

	AttributeNature getNature();

	MemberDetails getMember();

	/**
	 * An enum defining the nature (categorization) of a persistent attribute.
	 */
	enum AttributeNature {
		BASIC,
		EMBEDDED,
		ANY,
		TO_ONE,
		PLURAL
	}
}
