/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.intermediate.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.hibernate.MappingException;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.models.intermediate.spi.AssociationOverrideMetadata;
import org.hibernate.boot.models.intermediate.spi.AttributeOverrideMetadata;
import org.hibernate.boot.models.intermediate.spi.CallbacksMetadata;
import org.hibernate.boot.models.intermediate.spi.ConversionMetadata;
import org.hibernate.boot.models.intermediate.spi.EntityHierarchy;
import org.hibernate.boot.models.intermediate.spi.IdentifiableTypeMetadata;
import org.hibernate.boot.models.source.internal.AnnotationWrapperHelper;
import org.hibernate.boot.models.source.spi.AnnotationUsage;
import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.source.spi.ClassDetailsRegistry;
import org.hibernate.boot.models.source.spi.JpaAnnotations;
import org.hibernate.boot.models.source.spi.MethodDetails;
import org.hibernate.boot.models.spi.ModelProcessingContext;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.EntityListeners;

import static org.hibernate.boot.models.source.spi.AnnotationAttributeDescriptor.VALUE;
import static org.hibernate.boot.models.spi.ModelSourceLogging.MODEL_SOURCE_LOGGER;


/**
 * @author Steve Ebersole
 */
public abstract class AbstractIdentifiableTypeMetadata
		extends AbstractManagedTypeMetadata
		implements IdentifiableTypeMetadata {
	private final EntityHierarchy hierarchy;
	private final AbstractIdentifiableTypeMetadata superType;
	private final Set<IdentifiableTypeMetadata> subTypes = new HashSet<>();

	private final AccessType accessType;

	private final Map<AttributePath, ConversionMetadata> conversionInfoMap = new HashMap<>();
	private final Map<AttributePath, AttributeOverrideMetadata> attributeOverrideMap = new HashMap<>();
	private final Map<AttributePath, AssociationOverrideMetadata> associationOverrideMap = new HashMap<>();

	private List<CallbacksMetadata> collectedJpaCallbackSources;


	/**
	 * This form is intended for construction of root Entity, and any of
	 * its MappedSuperclasses
	 *
	 * @param classDetails The Entity/MappedSuperclass class descriptor
	 * @param hierarchy Details about the hierarchy
	 * @param isRootEntity Whether this descriptor is for the root entity itself, or
	 * one of its mapped-superclasses.
	 * @param accessType The default AccessType for the hierarchy
	 * @param processingContext The context
	 */
	public AbstractIdentifiableTypeMetadata(
			ClassDetails classDetails,
			EntityHierarchy hierarchy,
			boolean isRootEntity,
			AccessType accessType,
			Consumer<IdentifiableTypeMetadata> typeConsumer,
			ModelProcessingContext processingContext) {
		super( classDetails, processingContext );

		typeConsumer.accept( this );

		this.hierarchy = hierarchy;
		this.accessType = determineAccessType( accessType );

		// walk up
		this.superType = walkRootSuperclasses( classDetails, accessType, typeConsumer );

		if ( isRootEntity ) {
			// walk down
			walkSubclasses( classDetails, this, this.accessType, typeConsumer );
		}

		// the idea here is to collect up class-level annotations and to apply
		// the maps from supers
		collectConversionInfo();
		collectAttributeOverrides();
		collectAssociationOverrides();
	}

	private AccessType determineAccessType(AccessType defaultAccessType) {
		final AnnotationUsage<Access> annotation = getManagedClass().getAnnotation( JpaAnnotations.ACCESS );
		if ( annotation != null ) {
			return AnnotationWrapperHelper.extractValue( annotation, VALUE );
		}

		return defaultAccessType;
	}

	/**
	 * This form is intended for cases where the entity/mapped-superclass
	 * is part of the root subclass tree.
	 *
	 * @param classDetails The entity/mapped-superclass class descriptor
	 * @param hierarchy The hierarchy
	 * @param superType The metadata for the super type.
	 * @param processingContext The binding context
	 */
	public AbstractIdentifiableTypeMetadata(
			ClassDetails classDetails,
			EntityHierarchy hierarchy,
			AbstractIdentifiableTypeMetadata superType,
			Consumer<IdentifiableTypeMetadata> typeConsumer,
			ModelProcessingContext processingContext) {
		super( classDetails, processingContext );

		typeConsumer.accept( this );

		this.hierarchy = hierarchy;
		this.superType = superType;
		this.accessType = determineAccessType( superType.getAccessType() );

		// the idea here is to collect up class-level annotations and to apply
		// the maps from supers
		collectConversionInfo();
		collectAttributeOverrides();
		collectAssociationOverrides();
	}

	private AbstractIdentifiableTypeMetadata walkRootSuperclasses(
			ClassDetails classDetails,
			AccessType hierarchyAccessType,
			Consumer<IdentifiableTypeMetadata> typeConsumer) {
		final ClassDetails superTypeClassDetails = classDetails.getSuperType();
		if ( superTypeClassDetails == null ) {
			return null;
		}

		// make triple sure there is no @Entity annotation
		if ( isEntity( superTypeClassDetails ) ) {
			throw new MappingException(
					String.format(
							Locale.ENGLISH,
							"Unexpected @Entity [%s] as MappedSuperclass of entity hierarchy",
							superTypeClassDetails.getName()
					)
			);
		}
		else if ( isMappedSuperclass( superTypeClassDetails ) ) {
			final MappedSuperclassTypeMetadataImpl superType = new MappedSuperclassTypeMetadataImpl(
					superTypeClassDetails,
					getHierarchy(),
					hierarchyAccessType,
					typeConsumer,
					getModelProcessingContext()
			);
			superType.addSubclass( this );
			return superType;
		}
		else {
			// otherwise, we might have an "intermediate" subclass
			if ( superTypeClassDetails.getSuperType() != null ) {
				return walkRootSuperclasses( superTypeClassDetails, hierarchyAccessType, typeConsumer );
			}
			else {
				return null;
			}
		}
	}

	protected void addSubclass(IdentifiableTypeMetadata subclass) {
		subTypes.add( subclass );
	}

	protected boolean isMappedSuperclass(ClassDetails classDetails) {
		return classDetails.getAnnotation( JpaAnnotations.MAPPED_SUPERCLASS ) != null;
	}

	protected boolean isEntity(ClassDetails classDetails) {
		return classDetails.getAnnotation( JpaAnnotations.ENTITY ) != null;
	}

	private void walkSubclasses(
			ClassDetails classDetails,
			AbstractIdentifiableTypeMetadata superType,
			AccessType defaultAccessType,
			Consumer<IdentifiableTypeMetadata> typeConsumer) {
		final ClassDetailsRegistry classDetailsRegistry = getModelProcessingContext().getClassDetailsRegistry();
		classDetailsRegistry.forEachDirectSubType( classDetails.getName(), (subTypeManagedClass) -> {
			final AbstractIdentifiableTypeMetadata subTypeMetadata;
			if ( isEntity( subTypeManagedClass ) ) {
				subTypeMetadata = new EntityTypeMetadataImpl(
						subTypeManagedClass,
						getHierarchy(),
						superType,
						typeConsumer,
						getModelProcessingContext()
				);
				superType.addSubclass( subTypeMetadata );
			}
			else if ( isMappedSuperclass( subTypeManagedClass ) ) {
				subTypeMetadata = new MappedSuperclassTypeMetadataImpl(
						subTypeManagedClass,
						getHierarchy(),
						superType,
						typeConsumer,
						getModelProcessingContext()
				);
				superType.addSubclass( subTypeMetadata );
			}
			else {
				subTypeMetadata = superType;
			}

			walkSubclasses( subTypeManagedClass, subTypeMetadata, defaultAccessType, typeConsumer );
		} );
	}

	@Override
	public EntityHierarchy getHierarchy() {
		return hierarchy;
	}

	@Override
	public IdentifiableTypeMetadata getSuperType() {
		return superType;
	}

	@Override
	public boolean hasSubTypes() {
		// assume this is called only after its constructor is complete
		return !subTypes.isEmpty();
	}

	@Override
	public int getNumberOfSubTypes() {
		return subTypes.size();
	}

	@Override
	public void forEachSubType(Consumer<IdentifiableTypeMetadata> consumer) {
		// assume this is called only after its constructor is complete
		subTypes.forEach( consumer );
	}

	@Override
	public Iterable<IdentifiableTypeMetadata> getSubTypes() {
		// assume this is called only after its constructor is complete
		return subTypes;
	}

	@Override
	public AccessType getAccessType() {
		return accessType;
	}

	protected void collectConversionInfo() {
		// we only need to do this on root
	}

	protected void collectAttributeOverrides() {
		// we only need to do this on root
	}

	protected void collectAssociationOverrides() {
		// we only need to do this on root
	}

	@Override
	public ConversionMetadata locateConversionInfo(AttributePath attributePath) {
		return conversionInfoMap.get( attributePath );
	}

	@Override
	public AttributeOverrideMetadata locateAttributeOverride(AttributePath attributePath) {
		return attributeOverrideMap.get( attributePath );
	}

	@Override
	public AssociationOverrideMetadata locateAssociationOverride(AttributePath attributePath) {
		return associationOverrideMap.get( attributePath );
	}

	/**
	 * Obtain all JPA callbacks that should be applied for the given entity, including
	 * walking super-types.  This includes both method callbacks and listener callbacks.
	 *
	 * @return The callbacks.  {@code null} is never returned
	 */
	public List<CallbacksMetadata> getJpaCallbacks() {
		if ( collectedJpaCallbackSources == null ) {
			collectedJpaCallbackSources = collectJpaCallbacks();
		}
		return collectedJpaCallbackSources;
	}

	public void forEachJpaCallback(Consumer<CallbacksMetadata> consumer) {
		getJpaCallbacks().forEach( consumer );
	}

	private List<CallbacksMetadata> collectJpaCallbacks() {
		// JPA (2.1, section 3.5.5) explicitly defines the order callbacks should be called at run time
		// we follow that order in collecting the callbacks here...
		//		1) default entity listeners (by spec, these come from orm.xml)
		//		2) listeners, those defined on super types first
		//		3) methods, those defined on super types first
		//
		// within each "bucket" the listeners are executed in the order the are defined
		//
		// @ExcludeDefaultListeners and @ExcludeSuperClassListeners effect when to stop walking up durng
		// collection
		final ArrayList<CallbacksMetadata> callbacks = new ArrayList<>();


		//	1 - default entity listeners
		if ( getManagedClass().getAnnotation( JpaAnnotations.EXCLUDE_DEFAULT_LISTENERS ) == null ) {
			// exclusion of default listeners was *not* requested,
			// so add any default listeners defined as default in orm.xml
			// todo (annotation-source) : use the orm.xml values
//			final List<String> defaultListenerClassNames = ...;
//			for ( String defaultListenerClassName : defaultListenerClassNames ) {
//				final ClassInfo defaultListenerClassInfo = getLocalBindingContext().getJandexIndex().getClassByName(
//						DotName.createSimple( defaultListenerClassName )
//				);
//				collectCallbacks( defaultListenerClassInfo, true, callbacks );
//			}
		}

		// 2 - listeners, super-type-defined first
		collectEntityListenerCallbacks( this, callbacks );

		// 3 - methods, super-type-defined first
		collectMethodCallbacks( this, callbacks );

		return Collections.unmodifiableList( callbacks );
	}

	private void collectEntityListenerCallbacks(
			IdentifiableTypeMetadata typeMetadata,
			ArrayList<CallbacksMetadata> callbacks) {
		if ( getManagedClass().getAnnotation( JpaAnnotations.EXCLUDE_SUPERCLASS_LISTENERS ) == null ) {
			// walk super-type tree first, collecting callbacks
			if ( getSuperType() != null ) {
				collectEntityListenerCallbacks( getSuperType(), callbacks );
			}
		}

		final AnnotationUsage<EntityListeners> entityListenersAnnotation = getManagedClass().getAnnotation( JpaAnnotations.ENTITY_LISTENERS );
		if ( entityListenersAnnotation != null ) {
			final ClassDetails[] listenerClasses = AnnotationWrapperHelper.extractValue( entityListenersAnnotation, VALUE );
			for ( int i = 0; i < listenerClasses.length; i++ ) {
				collectCallbacks( listenerClasses[i], true, callbacks );
			}
		}
	}

	private void collectMethodCallbacks(
			IdentifiableTypeMetadata typeMetadata,
			ArrayList<CallbacksMetadata> callbacks) {
		if ( getManagedClass().getAnnotation( JpaAnnotations.EXCLUDE_SUPERCLASS_LISTENERS ) != null ) {
			// user requested exclusion of superclass-defined listeners
			return;
		}

		// walk super-type tree first, collecting callbacks
		if ( getSuperType() != null ) {
			collectMethodCallbacks( getSuperType(), callbacks );
		}

		collectCallbacks( typeMetadata.getManagedClass(), false, callbacks );
	}

	private void collectCallbacks(
			ClassDetails callbackClassInfo,
			boolean isListener,
			ArrayList<CallbacksMetadata> callbacks) {
		final MethodDetails prePersistCallback = MetadataHelper.findCallback(
				callbackClassInfo,
				JpaAnnotations.PRE_PERSIST,
				isListener
		);
		final MethodDetails preRemoveCallback = MetadataHelper.findCallback(
				callbackClassInfo,
				JpaAnnotations.PRE_REMOVE,
				isListener
		);
		final MethodDetails preUpdateCallback = MetadataHelper.findCallback(
				callbackClassInfo,
				JpaAnnotations.PRE_UPDATE,
				isListener
		);
		final MethodDetails postLoadCallback = MetadataHelper.findCallback(
				callbackClassInfo,
				JpaAnnotations.POST_LOAD,
				isListener
		);
		final MethodDetails postPersistCallback = MetadataHelper.findCallback(
				callbackClassInfo,
				JpaAnnotations.POST_PERSIST,
				isListener
		);
		final MethodDetails postRemoveCallback = MetadataHelper.findCallback(
				callbackClassInfo,
				JpaAnnotations.POST_REMOVE,
				isListener
		);
		final MethodDetails postUpdateCallback = MetadataHelper.findCallback(
				callbackClassInfo,
				JpaAnnotations.POST_UPDATE,
				isListener
		);

		if ( prePersistCallback == null
				&& preRemoveCallback == null
				&& preUpdateCallback == null
				&& postLoadCallback == null
				&& postPersistCallback == null
				&& postRemoveCallback == null
				&& postUpdateCallback == null ) {
			if ( isListener ) {
				MODEL_SOURCE_LOGGER.debugf(
						"Entity listener class [%s] named by @EntityListener on entity [%s] contained no callback methods",
						callbackClassInfo.getName(),
						getManagedClass().getName()
				);
			}
		}
		else {
			callbacks.add(
					new CallbacksMetadata(
							callbackClassInfo,
							isListener,
							prePersistCallback,
							preRemoveCallback,
							preUpdateCallback,
							postLoadCallback,
							postPersistCallback,
							postRemoveCallback,
							postUpdateCallback
					)
			);
		}
	}

	@Override
	public void registerConverter(
			AttributePath attributePath,
			ConversionMetadata conversionInfo) {
		final ConversionMetadata old = conversionInfoMap.put( attributePath, conversionInfo );
		if ( old != null ) {
			// todo : is this the best option?  should it be an exception instead?
			// todo : should probably also consider/prefer disabled
			MODEL_SOURCE_LOGGER.debugf(
					"@Convert-defined AttributeConverter"
			);
		}
	}

	@Override
	public void registerAttributeOverride(
			AttributePath attributePath,
			AttributeOverrideMetadata override) {
		if ( attributeOverrideMap.containsKey( attributePath ) ) {
			// an already registered path indicates that a higher context has already
			// done a registration; ignore the incoming one.
			MODEL_SOURCE_LOGGER.debugf(
					"On registration of @AttributeOverride we already had a " +
							"registered override for the given path [%s]; ignoring.  " +
							"This subsequent registration should indicate a 'lower " +
							"precedence' location."
			);
		}
		else {
			attributeOverrideMap.put( attributePath, override );
		}
	}

	@Override
	public void registerAssociationOverride(
			AttributePath attributePath,
			AssociationOverrideMetadata override) {
		associationOverrideMap.put( attributePath, override );
	}

//	@Override
//	public <A extends Annotation> AnnotationUsage<A> findAnnotation(AnnotationDescriptor<A> type) {
//		final AnnotationUsage<A> annotation = super.findAnnotation( type );
//		if ( annotation != null ) {
//			return annotation;
//		}
//
//		if ( superType != null ) {
//			return superType.findAnnotation( type );
//		}
//
//		return null;
//	}
//
//	@Override
//	public <A extends Annotation> List<AnnotationUsage<A>> findAnnotations(AnnotationDescriptor<A> type) {
//		final List<AnnotationUsage<A>> annotations = super.findAnnotations( type );
//		if ( CollectionHelper.isNotEmpty( annotations ) ) {
//			return annotations;
//		}
//
//		if ( superType != null ) {
//			return superType.findAnnotations( type );
//		}
//
//		return null;
//	}
//
//	@Override
//	public <A extends Annotation> void forEachAnnotation(AnnotationDescriptor<A> type, Consumer<AnnotationUsage<A>> consumer) {
//		final List<AnnotationUsage<A>> annotations = findAnnotations( type );
//		if ( CollectionHelper.isNotEmpty( annotations ) ) {
//			for ( int i = 0; i < annotations.size(); i++ ) {
//				consumer.accept( annotations.get( i ) );
//			}
//		}
//	}
}
