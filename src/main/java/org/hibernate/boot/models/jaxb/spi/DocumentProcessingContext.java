/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
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
