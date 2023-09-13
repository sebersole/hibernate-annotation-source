/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.jaxb.internal;

import org.hibernate.boot.jaxb.mapping.ManagedType;
import org.hibernate.boot.models.jaxb.spi.ClassProcessingContext;
import org.hibernate.boot.models.jaxb.spi.DocumentProcessingContext;

/**
 * Models details about the JAXB node for an entity, mapped-superclass or embeddable class
 *
 * @author Steve Ebersole
 */
public class ClassProcessingContextImpl implements ClassProcessingContext {
	private final ManagedType jaxbNode;
	private final DocumentProcessingContext documentContext;

	public ClassProcessingContextImpl(ManagedType jaxbNode, DocumentProcessingContext documentContext) {
		this.documentContext = documentContext;
		this.jaxbNode = jaxbNode;
	}

	@Override
	public ManagedType getClassNode() {
		return jaxbNode;
	}

	@Override
	public DocumentProcessingContext getDocumentProcessingContext() {
		return documentContext;
	}
}
