/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.boot.models.bind.complete;

/**
 * Entity used as a metadata-complete mapping
 *
 * @author Steve Ebersole
 */
public class XmlMappedEntity {
	private Integer id;
	private String name;

	public XmlMappedEntity() {
	}

	public XmlMappedEntity(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
