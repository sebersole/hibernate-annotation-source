/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.boot.models;

import java.sql.Types;

import org.hibernate.usertype.UserTypeSupport;

/**
 * @author Steve Ebersole
 */
public class UserTypeImpl extends UserTypeSupport<String> {
	public UserTypeImpl() {
		super( String.class, Types.VARCHAR );
	}
}
