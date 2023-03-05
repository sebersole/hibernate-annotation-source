/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.intermediate.spi;

import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.models.spi.LocalProcessingContext;
import org.hibernate.boot.models.spi.ModelProcessingContext;

/**
 * Models a context for processing the boot {@linkplain org.hibernate.boot.models.source source model}
 * and creating boot {@linkplain org.hibernate.boot.models.intermediate intermediate model}
 *
 * @author Steve Ebersole
 */
public interface ModelBuildingContext extends LocalProcessingContext<ManagedTypeMetadata> {
	ManagedTypeMetadata getScope();

	default ModelProcessingContext getModelProcessingContext() {
		return getScope().getModelBuildingContext().getModelProcessingContext();
	}

	default Database getDatabase() {
		return getModelProcessingContext().getMetadataBuildingContext().getMetadataCollector().getDatabase();
	}

	default Namespace getDefaultNamespace() {
		return getDatabase().getDefaultNamespace();
	}
}
