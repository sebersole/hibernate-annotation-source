/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.bind.internal.global;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.boot.internal.ClassmateContext;
import org.hibernate.boot.jaxb.mapping.JaxbCollectionUserTypeRegistration;
import org.hibernate.boot.jaxb.mapping.JaxbCompositeUserTypeRegistration;
import org.hibernate.boot.jaxb.mapping.JaxbConfigurationParameter;
import org.hibernate.boot.jaxb.mapping.JaxbConverterRegistration;
import org.hibernate.boot.jaxb.mapping.JaxbEmbeddableInstantiatorRegistration;
import org.hibernate.boot.jaxb.mapping.JaxbEntityMappings;
import org.hibernate.boot.jaxb.mapping.JaxbJavaTypeRegistration;
import org.hibernate.boot.jaxb.mapping.JaxbJdbcTypeRegistration;
import org.hibernate.boot.jaxb.mapping.JaxbUserTypeRegistration;
import org.hibernate.boot.model.convert.spi.RegisteredConversion;
import org.hibernate.boot.models.jaxb.spi.DocumentProcessingContext;
import org.hibernate.boot.models.source.internal.AnnotationWrapperHelper;
import org.hibernate.boot.models.source.spi.AnnotationTarget;
import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.source.spi.ClassDetailsRegistry;
import org.hibernate.boot.models.spi.ModelProcessingContext;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.InFlightMetadataCollector.CollectionTypeRegistrationDescriptor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.metamodel.CollectionClassification;
import org.hibernate.resource.beans.internal.FallbackBeanInstanceProducer;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.jdbc.JdbcType;

import static org.hibernate.boot.models.source.spi.AnnotationAttributeDescriptor.VALUE;
import static org.hibernate.boot.models.source.spi.HibernateAnnotations.COLLECTION_TYPE_REG;
import static org.hibernate.boot.models.source.spi.HibernateAnnotations.COMPOSITE_TYPE_REG;
import static org.hibernate.boot.models.source.spi.HibernateAnnotations.CONVERTER_REG;
import static org.hibernate.boot.models.source.spi.HibernateAnnotations.EMBEDDABLE_INSTANTIATOR_REG;
import static org.hibernate.boot.models.source.spi.HibernateAnnotations.JAVA_TYPE_REG;
import static org.hibernate.boot.models.source.spi.HibernateAnnotations.JDBC_TYPE_REG;
import static org.hibernate.boot.models.source.spi.HibernateAnnotations.TYPE_REG;

/**
 * Processes global type-contribution metadata
 *
 * @see org.hibernate.annotations.JavaTypeRegistration
 * @see org.hibernate.annotations.JdbcTypeRegistration
 * @see org.hibernate.annotations.ConverterRegistration
 * @see org.hibernate.annotations.EmbeddableInstantiatorRegistration
 * @see org.hibernate.annotations.TypeRegistration
 * @see org.hibernate.annotations.CompositeTypeRegistration
 * @see org.hibernate.annotations.CollectionTypeRegistration
 *
 * @author Steve Ebersole
 */
public class TypeContributionProcessor {
	public static void processTypeContributions(
			AnnotationTarget annotationTarget,
			ModelProcessingContext processingContext) {
		final TypeContributionProcessor processor = new TypeContributionProcessor( processingContext );
		processor.processJavaTypeRegistrations( annotationTarget );
		processor.processJdbcTypeRegistrations( annotationTarget );
		processor.processAttributeConverterRegistrations( annotationTarget );
		processor.processUserTypeRegistrations( annotationTarget );
		processor.processCompositeUserTypeRegistrations( annotationTarget );
		processor.processCollectionTypeRegistrations( annotationTarget );
		processor.processEmbeddableInstantiatorRegistrations( annotationTarget );
	}

	public static void processTypeContributions(
			JaxbEntityMappings root,
			DocumentProcessingContext processingContext) {
		final TypeContributionProcessor processor = new TypeContributionProcessor( processingContext.getModelProcessingContext() );
		processor.processJavaTypeRegistrations( root.getJavaTypeRegistrations() );
		processor.processJdbcTypeRegistrations( root.getJdbcTypeRegistrations() );
		processor.processAttributeConverterRegistrations( root.getConverterRegistrations() );
		processor.processUserTypeRegistrations( root.getUserTypeRegistrations() );
		processor.processCompositeUserTypeRegistrations( root.getCompositeUserTypeRegistrations() );
		processor.processCollectionTypeRegistrations( root.getCollectionUserTypeRegistrations() );
		processor.processEmbeddableInstantiatorRegistrations( root.getEmbeddableInstantiatorRegistrations() );
	}

	private final ModelProcessingContext processingContext;

	private final ClassDetailsRegistry classDetailsRegistry;
	private final InFlightMetadataCollector metadataCollector;
	private final ClassmateContext classmateContext;

	public TypeContributionProcessor(ModelProcessingContext processingContext) {
		this.processingContext = processingContext;

		this.classDetailsRegistry = processingContext.getClassDetailsRegistry();
		this.metadataCollector = processingContext.getMetadataBuildingContext().getMetadataCollector();
		this.classmateContext = processingContext.getMetadataBuildingContext()
				.getBootstrapContext()
				.getClassmateContext();
	}

	private void processJavaTypeRegistrations(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( JAVA_TYPE_REG, (usage) -> {
			final ClassDetails javaType = AnnotationWrapperHelper.extractValue( usage, "javaType" );
			final ClassDetails descriptorClass = AnnotationWrapperHelper.extractValue( usage, "descriptorClass" );

			metadataCollector.addJavaTypeRegistration(
					javaType.toJavaClass(),
					FallbackBeanInstanceProducer.INSTANCE.produceBeanInstance( descriptorClass.toJavaClass() )
			);
		} );
	}

	private void processJavaTypeRegistrations(List<JaxbJavaTypeRegistration> registrations) {
		if ( CollectionHelper.isEmpty( registrations ) ) {
			return;
		}

		registrations.forEach( (reg) -> {
			final ClassDetails domainClass = classDetailsRegistry.resolveClassDetails( reg.getClazz() );
			final ClassDetails descriptorClassDetails = classDetailsRegistry.resolveClassDetails( reg.getDescriptor() );
			final JavaType<?> descriptor = FallbackBeanInstanceProducer.INSTANCE.produceBeanInstance( descriptorClassDetails.toJavaClass() );

			metadataCollector.addJavaTypeRegistration( domainClass.toJavaClass(), descriptor );
		} );
	}

	private void processJdbcTypeRegistrations(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( JDBC_TYPE_REG, (usage) -> {
			final int registrationCode = AnnotationWrapperHelper.extractValue( usage, "registrationCode", Integer.MIN_VALUE );
			final ClassDetails descriptor = AnnotationWrapperHelper.extractValue( usage, VALUE );

			final JdbcType jdbcType = FallbackBeanInstanceProducer.INSTANCE.produceBeanInstance( descriptor.toJavaClass() );
			final int typeCode = registrationCode == Integer.MIN_VALUE
					? jdbcType.getJdbcTypeCode()
					: registrationCode;

			metadataCollector.addJdbcTypeRegistration( typeCode, jdbcType );
		} );
	}

	private void processJdbcTypeRegistrations(List<JaxbJdbcTypeRegistration> registrations) {
		if ( CollectionHelper.isEmpty( registrations ) ) {
			return;
		}

		registrations.forEach( (reg) -> {
			final Integer code = reg.getCode();
			final ClassDetails descriptorClassDetails = classDetailsRegistry.resolveClassDetails( reg.getDescriptor() );
			final JdbcType descriptor = FallbackBeanInstanceProducer.INSTANCE.produceBeanInstance( descriptorClassDetails.toJavaClass() );

			final int registrationCode = code == null
					? descriptor.getJdbcTypeCode()
					: code;
			metadataCollector.addJdbcTypeRegistration( registrationCode, descriptor );
		} );
	}

	private void processAttributeConverterRegistrations(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( CONVERTER_REG, (usage) -> {
			final ClassDetails domainType = AnnotationWrapperHelper.extractValue( usage, "domainType" );
			final ClassDetails converterType = AnnotationWrapperHelper.extractValue( usage, "converter" );
			final boolean autoApply = AnnotationWrapperHelper.extractValue( usage, "autoApply", true );

			metadataCollector.getConverterRegistry().addRegisteredConversion( new RegisteredConversion(
					domainType.toJavaClass(),
					converterType.toJavaClass(),
					autoApply,
					processingContext.getMetadataBuildingContext()
			) );
		} );
	}

	private void processAttributeConverterRegistrations(List<JaxbConverterRegistration> registrations) {
		if ( CollectionHelper.isEmpty( registrations ) ) {
			return;
		}

		registrations.forEach( (reg) -> {
			final ClassDetails converterDetails = classDetailsRegistry.resolveClassDetails( reg.getConverter() );

			final Class<?> explicitDomainType;
			final String explicitDomainTypeName = reg.getClazz();
			if ( StringHelper.isNotEmpty( explicitDomainTypeName ) ) {
				explicitDomainType = classDetailsRegistry.resolveClassDetails( explicitDomainTypeName ).toJavaClass();
			}
			else {
				explicitDomainType = null;
			}

			final boolean autoApply = reg.isAutoApply();
			metadataCollector.getConverterRegistry().addRegisteredConversion( new RegisteredConversion(
					explicitDomainType,
					converterDetails.toJavaClass(),
					autoApply,
					processingContext.getMetadataBuildingContext()
			) );
		} );
	}

	private void processEmbeddableInstantiatorRegistrations(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( EMBEDDABLE_INSTANTIATOR_REG, (usage) -> {
			final ClassDetails embeddableClass = AnnotationWrapperHelper.extractValue( usage, "embeddableClass" );
			final ClassDetails instantiatorClass = AnnotationWrapperHelper.extractValue( usage, "instantiator" );

			metadataCollector.registerEmbeddableInstantiator( embeddableClass.toJavaClass(), instantiatorClass.toJavaClass() );
		} );
	}

	private void processEmbeddableInstantiatorRegistrations(List<JaxbEmbeddableInstantiatorRegistration> registrations) {
		if ( CollectionHelper.isEmpty( registrations ) ) {
			return;
		}

		registrations.forEach( (reg) -> {
			final ClassDetails embeddableClassDetails = classDetailsRegistry.resolveClassDetails( reg.getEmbeddableClass() );
			final ClassDetails instantiatorClassDetails = classDetailsRegistry.resolveClassDetails( reg.getInstantiator() );
			metadataCollector.registerEmbeddableInstantiator( embeddableClassDetails.toJavaClass(), instantiatorClassDetails.toJavaClass() );
		} );
	}

	private void processUserTypeRegistrations(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( TYPE_REG, (usage) -> {
			final ClassDetails basicClass = AnnotationWrapperHelper.extractValue( usage, "basicClass" );
			final ClassDetails userTypeClass = AnnotationWrapperHelper.extractValue( usage, "userType" );

			metadataCollector.registerUserType( basicClass.toJavaClass(), userTypeClass.toJavaClass() );
		} );
	}

	private void processUserTypeRegistrations(List<JaxbUserTypeRegistration> registrations) {
		if ( CollectionHelper.isEmpty( registrations ) ) {
			return;
		}

		registrations.forEach( (reg) -> {
			final ClassDetails domainTypeDetails = classDetailsRegistry.resolveClassDetails( reg.getClazz() );
			final ClassDetails descriptorDetails = classDetailsRegistry.resolveClassDetails( reg.getDescriptor() );

			metadataCollector.registerUserType( domainTypeDetails.toJavaClass(), descriptorDetails.toJavaClass() );
		} );
	}

	private void processCompositeUserTypeRegistrations(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( COMPOSITE_TYPE_REG, (usage) -> {
			final ClassDetails embeddableClass = AnnotationWrapperHelper.extractValue( usage, "embeddableClass" );
			final ClassDetails userTypeClass = AnnotationWrapperHelper.extractValue( usage, "userType" );

			metadataCollector.registerCompositeUserType( embeddableClass.toJavaClass(), userTypeClass.toJavaClass() );
		} );
	}

	private void processCompositeUserTypeRegistrations(List<JaxbCompositeUserTypeRegistration> registrations) {
		if ( CollectionHelper.isEmpty( registrations ) ) {
			return;
		}

		registrations.forEach( (reg) -> {
			final ClassDetails domainTypeDetails = classDetailsRegistry.resolveClassDetails( reg.getClazz() );
			final ClassDetails descriptorDetails = classDetailsRegistry.resolveClassDetails( reg.getDescriptor() );

			metadataCollector.registerCompositeUserType( domainTypeDetails.toJavaClass(), descriptorDetails.toJavaClass() );
		} );
	}

	private void processCollectionTypeRegistrations(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( COLLECTION_TYPE_REG, (usage) -> {
			final CollectionClassification classification = AnnotationWrapperHelper.extractValue( usage, "classification" );
			final ClassDetails typeType = AnnotationWrapperHelper.extractValue( usage, "type" );
			final Map<String,String> parameterMap = extractParameterMap( AnnotationWrapperHelper.extractValue( usage, "parameters" ) );

			metadataCollector.addCollectionTypeRegistration(
					classification,
					new CollectionTypeRegistrationDescriptor( typeType.toJavaClass(), parameterMap )
			);
		} );
	}

	private void processCollectionTypeRegistrations(List<JaxbCollectionUserTypeRegistration> registrations) {
		if ( CollectionHelper.isEmpty( registrations ) ) {
			return;
		}

		registrations.forEach( (reg) -> {
			final ClassDetails descriptorDetails = classDetailsRegistry.resolveClassDetails( reg.getDescriptor() );
			final Map<String,String> parameterMap = extractParameterMap( reg.getParameters() );

			metadataCollector.addCollectionTypeRegistration(
					reg.getClassification(),
					new CollectionTypeRegistrationDescriptor( descriptorDetails.toJavaClass(), parameterMap )
			);
		} );
	}

	private Map<String, String> extractParameterMap(List<JaxbConfigurationParameter> parameters) {
		if ( CollectionHelper.isEmpty( parameters ) ) {
			return Collections.emptyMap();
		}

		final Map<String,String> result = new HashMap<>();
		parameters.forEach( parameter -> result.put( parameter.getName(), parameter.getValue() ) );
		return result;
	}
}
