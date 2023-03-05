/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
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
