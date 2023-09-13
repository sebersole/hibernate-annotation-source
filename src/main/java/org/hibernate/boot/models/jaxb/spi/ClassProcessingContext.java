/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.jaxb.spi;

import org.hibernate.boot.jaxb.mapping.ManagedType;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.models.spi.LocalProcessingContext;
import org.hibernate.boot.models.spi.ModelProcessingContext;

/**
 * Specialized processing context specific to a particular entity, mapped-superclass or embeddable node
 * within a XML document

 * @author Steve Ebersole
 */
public interface ClassProcessingContext extends LocalProcessingContext<ManagedType> {
	/**
	 * The entity, mapped-superclass or embeddable class node
	 */
	ManagedType getClassNode();

	@Override
	default ManagedType getScope() {
		return getClassNode();
	}

	/**
	 * Processing context for the containing XML document
	 */
	DocumentProcessingContext getDocumentProcessingContext();

	default ModelProcessingContext getModelProcessingContext() {
		return getDocumentProcessingContext().getModelProcessingContext();
	}

	default Database getDatabase() {
		return getModelProcessingContext().getMetadataBuildingContext().getMetadataCollector().getDatabase();
	}

	default Namespace getDefaultNamespace() {
		return getDatabase().getDefaultNamespace();
	}
}
