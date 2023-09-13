/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.boot.models.bind;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Very simple entity, with lots of implicit mappings to test
 * resolution of those implicit values
 *
 * @author Steve Ebersole
 */
@Entity
public class BasicEntity {
	@Id
	private Integer id;
	private String name;
}
