/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.boot.models.bind.global;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.BitSet;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

/**
 * @author Steve Ebersole
 */
public class BitSetUserType implements UserType<BitSet> {
	@Override
	public int getSqlType() {
		return 0;
	}

	@Override
	public Class<BitSet> returnedClass() {
		return null;
	}

	@Override
	public boolean equals(BitSet x, BitSet y) {
		return false;
	}

	@Override
	public int hashCode(BitSet x) {
		return 0;
	}

	@Override
	public BitSet nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
		return null;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, BitSet value, int index, SharedSessionContractImplementor session) throws SQLException {

	}

	@Override
	public BitSet deepCopy(BitSet value) {
		return null;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(BitSet value) {
		return null;
	}

	@Override
	public BitSet assemble(Serializable cached, Object owner) {
		return null;
	}
}
