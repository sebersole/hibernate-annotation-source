/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */

/**
 * During bootstrap, Hibernate processes application classes and XML mappings in
 * a number of phases ultimately producing the {@linkplain org.hibernate.mapping boot metamodel}:<ol>
 *     <li>
 *         First is the {@linkplain org.hibernate.boot.models.source source model} which is basically a
 *         mirror of the Java model ({@link java.lang.Class}, {@link java.lang.reflect.Method}, etc).
 *     </li>
 *     <li>
 *         Next, is the {@linkplain  org.hibernate.boot.models.intermediate intermediate model}
 *         which models a simple categorization of the source model
 *     </li>
 * </ol>
 *
 * @author Steve Ebersole
 */
package org.hibernate.boot.models;
