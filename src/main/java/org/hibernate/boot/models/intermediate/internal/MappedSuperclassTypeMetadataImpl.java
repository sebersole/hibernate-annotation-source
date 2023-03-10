/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.intermediate.internal;

import java.util.List;
import java.util.function.Consumer;

import org.hibernate.boot.models.intermediate.spi.AttributeMetadata;
import org.hibernate.boot.models.intermediate.spi.EntityHierarchy;
import org.hibernate.boot.models.intermediate.spi.IdentifiableTypeMetadata;
import org.hibernate.boot.models.intermediate.spi.MappedSuperclassTypeMetadata;
import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.spi.ModelProcessingContext;

import jakarta.persistence.AccessType;

/**
 * @author Steve Ebersole
 */
public class MappedSuperclassTypeMetadataImpl
		extends AbstractIdentifiableTypeMetadata
		implements MappedSuperclassTypeMetadata {

	private final List<AttributeMetadata> attributeList;

	public MappedSuperclassTypeMetadataImpl(
			ClassDetails classDetails,
			EntityHierarchy hierarchy,
			AccessType defaultAccessType,
			Consumer<IdentifiableTypeMetadata> typeConsumer,
			ModelProcessingContext processingContext) {
		super( classDetails, hierarchy, false, defaultAccessType, typeConsumer, processingContext );

		this.attributeList = resolveAttributes();
	}

	public MappedSuperclassTypeMetadataImpl(
			ClassDetails classDetails,
			EntityHierarchy hierarchy,
			AbstractIdentifiableTypeMetadata superType,
			Consumer<IdentifiableTypeMetadata> typeConsumer,
			ModelProcessingContext processingContext) {
		super( classDetails, hierarchy, superType, typeConsumer, processingContext );

		this.attributeList = resolveAttributes();
	}

	@Override
	protected List<AttributeMetadata> attributeList() {
		return attributeList;
	}
}
