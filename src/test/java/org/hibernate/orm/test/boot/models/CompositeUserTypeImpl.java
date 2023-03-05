/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.orm.test.boot.models;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.ValueAccess;
import org.hibernate.usertype.CompositeUserType;

/**
 * @author Steve Ebersole
 */
public class CompositeUserTypeImpl implements CompositeUserType<Object> {

	@Override
	public Object getPropertyValue(Object component, int property) throws HibernateException {
		return null;
	}

	@Override
	public Object instantiate(ValueAccess values, SessionFactoryImplementor sessionFactory) {
		return null;
	}

	@Override
	public Class<?> embeddable() {
		return null;
	}

	@Override
	public Class returnedClass() {
		return null;
	}

	@Override
	public boolean equals(Object x, Object y) {
		return false;
	}

	@Override
	public int hashCode(Object x) {
		return 0;
	}

	@Override
	public Object deepCopy(Object value) {
		return null;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Object value) {
		return null;
	}

	@Override
	public Object assemble(Serializable cached, Object owner) {
		return null;
	}

	@Override
	public Object replace(Object detached, Object managed, Object owner) {
		return null;
	}
}
