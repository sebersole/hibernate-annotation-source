/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.boot.models;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.ValueAccess;

/**
 * @author Steve Ebersole
 */
public class EmbeddableInstantiatorImpl implements org.hibernate.metamodel.spi.EmbeddableInstantiator {
	@Override
	public Object instantiate(ValueAccess valueAccess, SessionFactoryImplementor sessionFactory) {
		return null;
	}

	@Override
	public boolean isInstance(Object object, SessionFactoryImplementor sessionFactory) {
		return false;
	}

	@Override
	public boolean isSameClass(Object object, SessionFactoryImplementor sessionFactory) {
		return false;
	}
}
