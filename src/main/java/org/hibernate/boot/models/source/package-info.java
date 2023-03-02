/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */

/**
 * Hibernate's boot time source model, which is a metamodel describing all known classes and XML mappings.
 * <p/>
 * This metamodel is largely a de-typed
 * Support for dealing with {@linkplain org.hibernate.boot.model.process.spi.ManagedResources "managed resources"},
 * which describes all known classes and XML mappings.
 *
 *
 * Support for dealing with {@linkplain org.hibernate.boot.model.process.spi.ManagedResources "managed resources"},
 * which describes all known classes and XML mappings.
 * <p/>
 * Metamodel describing {@linkplain org.hibernate.boot.annotations.source.spi.ClassDetails "managed classes"},
 * Hibernate's boot time source model.
 * <p/>
 *
 * Metamodel describing {@linkplain org.hibernate.boot.annotations.source.spi.ClassDetails "managed classes"},
 * their {@linkplain org.hibernate.boot.annotations.source.spi.FieldDetails fields} and
 * {@linkplain org.hibernate.boot.annotations.source.spi.MethodDetails methods} as well as
 * {@linkplain org.hibernate.boot.annotations.source.spi.AnnotationUsage annotations} defined on them.
 * <p/>
 * This source model is a mirror of {@link java.lang.Class}, {@link java.lang.reflect.Field},
 * {@link java.lang.reflect.Method} and {@link java.lang.annotation.Annotation}.  The 2 main reasons for
 * this are -<ol>
 *     <li>
 *         Allows for "dynamic models", e.g. {@linkplain org.hibernate.metamodel.RepresentationMode#MAP MAP} models
 *     </li>
 *     <li>
 *         It avoids prematurely loading these references into the {@link java.lang.ClassLoader}.
 *     </li>
 * </ol>
 *
 * @see org.hibernate.boot.model.process.spi.ManagedResources
 *
 * @author Steve Ebersole
 */
package org.hibernate.boot.models.source;
