/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.jaxb.spi;

import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.mapping.JaxbEntityMappings;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.models.spi.LocalProcessingContext;
import org.hibernate.boot.models.spi.ModelProcessingContext;

/**
 * Processing context for a particular XML document
 *
 * @author Steve Ebersole
 */
public interface DocumentProcessingContext extends LocalProcessingContext<JaxbEntityMappings> {
	JaxbEntityMappings getXmlMapping();

	@Override
	default JaxbEntityMappings getScope() {
		return getXmlMapping();
	}

	Origin getXmlOrigin();

	default boolean isComplete() {
		return getXmlMapping().getPersistenceUnitMetadata() != null
				&& getXmlMapping().getPersistenceUnitMetadata().getXmlMappingMetadataComplete() != null;
	}

	default Database getDatabase() {
		return getModelProcessingContext().getMetadataBuildingContext().getMetadataCollector().getDatabase();
	}

	default Namespace getDefaultNamespace() {
		return getDatabase().getDefaultNamespace();
	}
}
