/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

/**
 * @author Steve Ebersole
 */
@GenericGenerator(name = "gen1")
@JavaTypeRegistration( javaType = String.class, descriptorClass = CustomStringJavaType.class )
@JdbcTypeRegistration(CustomVarcharJdbcType.class)
@TypeRegistration(basicClass = BitSet.class, userType = BitSetUserType.class)
@ConverterRegistration(domainType = Map.class, converter = MapConverter.class)
@NamedQuery(name = "query1", query = "select * from BasicEntity")
@NamedNativeQuery(name = "query2", query = "select * from BasicEntity")
@FilterDef(name = "filter-def1")
package org.hibernate.orm.test.boot.models.bind.global;

import java.util.BitSet;
import java.util.Map;

import org.hibernate.annotations.ConverterRegistration;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JavaTypeRegistration;
import org.hibernate.annotations.JdbcTypeRegistration;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.TypeRegistration;
import org.hibernate.orm.test.boot.models.MapConverter;
