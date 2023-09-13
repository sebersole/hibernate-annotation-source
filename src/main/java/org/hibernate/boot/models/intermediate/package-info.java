/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

/**
 * An intermediate model built from {@linkplain org.hibernate.boot.models.source annotated sources}
 * which categorizes the {@linkplain org.hibernate.boot.models.source.spi.ClassDetails managed classes}
 * as {@linkplain org.hibernate.boot.models.model.spi.EntityTypeMetadata entities},
 * {@linkplain org.hibernate.boot.models.model.spi.MappedSuperclassTypeMetadata mapped-superclasses},
 * etc.
 * <p/>
 * This intermediate model is ultimately used to {@linkplain org.hibernate.boot.models.bind build}
 * the {@linkplain org.hibernate.mapping boot model}
 *
 * @author Steve Ebersole
 */
package org.hibernate.boot.models.intermediate;
