/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.intermediate.internal;

import java.util.Locale;
import java.util.function.Consumer;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.boot.models.intermediate.spi.Caching;
import org.hibernate.boot.models.intermediate.spi.EntityHierarchy;
import org.hibernate.boot.models.intermediate.spi.EntityTypeMetadata;
import org.hibernate.boot.models.intermediate.spi.IdentifiableTypeMetadata;
import org.hibernate.boot.models.intermediate.spi.NaturalIdCaching;
import org.hibernate.boot.models.source.internal.AnnotationWrapperHelper;
import org.hibernate.boot.models.source.spi.AnnotationUsage;
import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.source.spi.HibernateAnnotations;
import org.hibernate.boot.models.source.spi.JpaAnnotations;
import org.hibernate.boot.models.spi.ModelProcessingContext;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cache.spi.access.AccessType;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.SharedCacheMode;

import static org.hibernate.boot.models.source.internal.AnnotationWrapperHelper.extractValue;
import static org.hibernate.boot.models.source.spi.AnnotationAttributeDescriptor.VALUE;
import static org.hibernate.boot.models.spi.ModelSourceLogging.MODEL_SOURCE_LOGGER;
import static org.hibernate.boot.models.spi.ModelSourceLogging.MODEL_SOURCE_LOGGER_DEBUG_ENABLED;

/**
 * @author Steve Ebersole
 */
public class EntityHierarchyImpl implements EntityHierarchy {
	private final EntityTypeMetadata rootEntityTypeMetadata;

	private final InheritanceType inheritanceType;
	private final Caching caching;
	private final NaturalIdCaching naturalIdCaching;

	// todo (annotation-source) : version?  row-id?  tenant-id?  others?

	public EntityHierarchyImpl(
			ClassDetails rootEntityClassDetails,
			jakarta.persistence.AccessType defaultAccessType,
			Consumer<IdentifiableTypeMetadata> typeConsumer,
			ModelProcessingContext processingContext) {
		this.rootEntityTypeMetadata = new EntityTypeMetadataImpl(
				rootEntityClassDetails,
				this,
				defaultAccessType,
				typeConsumer,
				processingContext
		);

		this.inheritanceType = determineInheritanceType( rootEntityTypeMetadata );

		rootEntityTypeMetadata.findAnnotation( JpaAnnotations.CACHEABLE );
		final AnnotationUsage<Cacheable> cacheableAnnotation = rootEntityClassDetails.getAnnotation( JpaAnnotations.CACHEABLE );
		final AnnotationUsage<Cache> cacheAnnotation = rootEntityClassDetails.getAnnotation( HibernateAnnotations.CACHE );
		final AnnotationUsage<NaturalIdCache> naturalIdCacheAnnotation = rootEntityClassDetails.getAnnotation( HibernateAnnotations.NATURAL_ID_CACHE );

		final MetadataBuildingContext metadataBuildingContext = processingContext.getMetadataBuildingContext();
		final AccessType implicitCacheAccessType = metadataBuildingContext.getBuildingOptions().getImplicitCacheAccessType();
		final SharedCacheMode sharedCacheMode = metadataBuildingContext.getBuildingOptions().getSharedCacheMode();

		switch ( sharedCacheMode ) {
			case NONE: {
				this.caching = new Caching( false );
				this.naturalIdCaching = new NaturalIdCaching( false );
				break;
			}
			case ALL: {
				this.caching = new Caching( cacheAnnotation, implicitCacheAccessType, rootEntityClassDetails.getName() );
				this.naturalIdCaching = new NaturalIdCaching( naturalIdCacheAnnotation, caching );
				break;
			}
			case DISABLE_SELECTIVE: {
				// Caching is disabled for `@Cacheable(false)`, enabled otherwise
				final boolean cached;
				if ( cacheableAnnotation == null ) {
					cached = true;
				}
				else {
					cached = AnnotationWrapperHelper.extractValue( cacheableAnnotation, VALUE );
				}
				if ( cached ) {
					this.caching = new Caching( cacheAnnotation, implicitCacheAccessType, rootEntityClassDetails.getName() );
					this.naturalIdCaching = new NaturalIdCaching( naturalIdCacheAnnotation, caching );
				}
				else {
					this.caching = new Caching( false );
					this.naturalIdCaching = new NaturalIdCaching( false );
				}
				break;
			}
			default: {
				// ENABLE_SELECTIVE
				// UNSPECIFIED

				// Caching is enabled for all entities for <code>Cacheable(true)</code>
				// is specified.  All other entities are not cached.
				final boolean cached = cacheableAnnotation != null
						&& AnnotationWrapperHelper.extractValue( cacheableAnnotation, VALUE, true );
				if ( cached ) {
					this.caching = new Caching( cacheAnnotation, implicitCacheAccessType, rootEntityClassDetails.getName() );
					this.naturalIdCaching = new NaturalIdCaching( naturalIdCacheAnnotation, caching );
				}
				else {
					this.caching = new Caching( false );
					this.naturalIdCaching = new NaturalIdCaching( false );
				}
			}
		}
	}

	private InheritanceType determineInheritanceType(EntityTypeMetadata root) {
		if ( MODEL_SOURCE_LOGGER_DEBUG_ENABLED ) {
			// Validate that there is no @Inheritance annotation further down the hierarchy
			ensureNoInheritanceAnnotationsOnSubclasses( root );
		}

		IdentifiableTypeMetadata current = root;
		while ( current != null ) {
			final InheritanceType inheritanceType = getLocallyDefinedInheritanceType( current.getManagedClass() );
			if ( inheritanceType != null ) {
				return inheritanceType;
			}

			current = current.getSuperType();
		}

		return InheritanceType.SINGLE_TABLE;
	}

	/**
	 * Find the InheritanceType from the locally defined {@link Inheritance} annotation,
	 * if one.  Returns {@code null} if {@link Inheritance} is not locally defined.
	 *
	 * @apiNote Used when building the {@link EntityHierarchy}
	 */
	private static InheritanceType getLocallyDefinedInheritanceType(ClassDetails managedClass) {
		final AnnotationUsage<Inheritance> localAnnotation = managedClass.getAnnotation( JpaAnnotations.INHERITANCE );
		if ( localAnnotation == null ) {
			return null;
		}

		return AnnotationWrapperHelper.extractValue( localAnnotation, "strategy" );
	}

	private void ensureNoInheritanceAnnotationsOnSubclasses(IdentifiableTypeMetadata type) {
		type.forEachSubType( (subType) -> {
			if ( getLocallyDefinedInheritanceType( subType.getManagedClass() ) != null ) {
				MODEL_SOURCE_LOGGER.debugf(
						"@javax.persistence.Inheritance was specified on non-root entity [%s]; ignoring...",
						type.getManagedClass().getName()
				);
			}
			ensureNoInheritanceAnnotationsOnSubclasses( subType );
		} );
	}

	@Override
	public EntityTypeMetadata getRoot() {
		return rootEntityTypeMetadata;
	}

	@Override
	public InheritanceType getInheritanceType() {
		return inheritanceType;
	}

	@Override
	public Caching getCaching() {
		return caching;
	}

	@Override
	public NaturalIdCaching getNaturalIdCaching() {
		return naturalIdCaching;
	}

	@Override
	public String toString() {
		return String.format(
				Locale.ROOT,
				"EntityHierarchyImpl(`%s` (%s))",
				rootEntityTypeMetadata.getEntityName(),
				inheritanceType.name()
		);
	}
}
