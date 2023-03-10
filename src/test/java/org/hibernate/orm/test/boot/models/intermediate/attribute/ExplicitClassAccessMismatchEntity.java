/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.orm.test.boot.models.intermediate.attribute;

import jakarta.persistence.Access;
import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import static jakarta.persistence.AccessType.PROPERTY;

/**
 * The class specifies PROPERTY but the annotations are defined on fields
 *
 * @author Steve Ebersole
 */
@Entity
@Access(PROPERTY)
public class ExplicitClassAccessMismatchEntity {
	@Id
	private Integer id;
	@Basic
	private String name;
}
