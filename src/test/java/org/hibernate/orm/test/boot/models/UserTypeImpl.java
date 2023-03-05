/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
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
