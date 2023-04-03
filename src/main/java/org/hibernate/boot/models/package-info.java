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
 *         First is the {@linkplain org.hibernate.boot.models.source source metamodel} which is basically a
 *         mirror of the Java model ({@link java.lang.Class}, {@link java.lang.reflect.Method}, etc).
 *     </li>
 *     <li>
 *         Next, is the {@linkplain  org.hibernate.boot.models.intermediate intermediate metamodel}
 *         which models a simple categorization of the source metamodel
 *     </li>
 *     <li>
 *         The {@linkplain  org.hibernate.boot.models.intermediate intermediate metamodel} is then
 *         {@linkplain  org.hibernate.boot.models.bind bound} into the
 *         {@linkplain org.hibernate.mapping boot metamodel}
 *     </li>
 * </ol>
 *
 * @author Steve Ebersole
 */
@Incubating
package org.hibernate.boot.models;

import org.hibernate.Incubating;
