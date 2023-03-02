/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.function.Consumer;

import org.hibernate.HibernateException;
import org.hibernate.boot.models.source.internal.reflection.ClassDetailsBuilderImpl;
import org.hibernate.boot.models.source.internal.reflection.ClassDetailsImpl;
import org.hibernate.boot.models.source.spi.AnnotationDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationDescriptorRegistry;
import org.hibernate.boot.models.source.spi.AnnotationUsage;
import org.hibernate.boot.models.source.spi.ClassDetailsRegistry;
import org.hibernate.boot.models.spi.AnnotationProcessingContext;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.internal.util.collections.CollectionHelper;

import jakarta.persistence.AttributeConverter;

/**
 * Standard implementation of AnnotationProcessingContext
 *
 * @author Steve Ebersole
 */
public class AnnotationProcessingContextImpl implements AnnotationProcessingContext {
	private final AnnotationDescriptorRegistryImpl descriptorRegistry;
	private final ClassDetailsRegistry classDetailsRegistry;
	private final MetadataBuildingContext buildingContext;

	private final Map<AnnotationDescriptor<?>,List<AnnotationUsage<?>>> annotationUsageMap = new HashMap<>();

	public AnnotationProcessingContextImpl(MetadataBuildingContext buildingContext) {
		this.buildingContext = buildingContext;
		this.descriptorRegistry = new AnnotationDescriptorRegistryImpl( this );
		this.classDetailsRegistry = new ClassDetailsRegistry( this );
		
		AnnotationHelper.forEachOrmAnnotation( (annotationDescriptor) -> {
			descriptorRegistry.register( annotationDescriptor );
			classDetailsRegistry.addManagedClass( new ClassDetailsImpl( annotationDescriptor.getAnnotationType(), this ) );
		} );

		// todo (annotation-source) : add any standard Java types here up front
		//  	- anything we know we will never have to enhance really.
		//		- possibly leverage `buildingContext.getBootstrapContext().getTypeConfiguration().getBasicTypeRegistry()`

		primeClassDetails( String.class );
		primeClassDetails( Boolean.class );
		primeClassDetails( Enum.class );
		primeClassDetails( Byte.class );
		primeClassDetails( Short.class );
		primeClassDetails( Integer.class );
		primeClassDetails( Long.class );
		primeClassDetails( Double.class );
		primeClassDetails( Float.class );
		primeClassDetails( BigInteger.class );
		primeClassDetails( BigDecimal.class );
		primeClassDetails( Blob.class );
		primeClassDetails( Clob.class );
		primeClassDetails( NClob.class );
		primeClassDetails( Instant.class );
		primeClassDetails( LocalDate.class );
		primeClassDetails( LocalTime.class );
		primeClassDetails( LocalDateTime.class );
		primeClassDetails( OffsetTime.class );
		primeClassDetails( OffsetDateTime.class );
		primeClassDetails( ZonedDateTime.class );
		primeClassDetails( java.util.Date.class );
		primeClassDetails( java.sql.Date.class );
		primeClassDetails( java.sql.Time.class );
		primeClassDetails( java.sql.Timestamp.class );
		primeClassDetails( URL.class );
		primeClassDetails( Collection.class );
		primeClassDetails( Set.class );
		primeClassDetails( List.class );
		primeClassDetails( Map.class );
		primeClassDetails( Comparator.class );
		primeClassDetails( Comparable.class );
		primeClassDetails( SortedSet.class );
		primeClassDetails( SortedMap.class );

		primeClassDetails( AttributeConverter.class );
	}

	private void primeClassDetails(Class<?> javaType) {
		classDetailsRegistry.resolveManagedClass(
				javaType.getName(),
				ClassDetailsBuilderImpl::buildClassDetailsStatic
		);
	}

	@Override
	public AnnotationDescriptorRegistry getAnnotationDescriptorRegistry() {
		return descriptorRegistry;
	}

	@Override
	public ClassDetailsRegistry getClassDetailsRegistry() {
		return classDetailsRegistry;
	}

	@Override
	public MetadataBuildingContext getMetadataBuildingContext() {
		return buildingContext;
	}

	@Override
	public void registerUsage(AnnotationUsage<?> usage) {
		// todo (annotation-source) : we only care about this in specific cases.
		//		this feeds a Map used to locate annotations regardless of target.
		//		this is used when locating "global" annotations such as generators,
		//		named-queries, etc.
		//		+
		//		an option to limit what we cache would be to add a flag to AnnotationDescriptor
		//		to indicate whether the annotation is "globally resolvable".

		// register the usage under the appropriate descriptor.
		//
		// if the incoming value is a usage of a "repeatable container", skip the
		// registration - the repetitions themselves are what we are interested in,
		// and they will get registered themselves

		final AnnotationDescriptor<?> incomingUsageDescriptor = usage.getAnnotationDescriptor();
		final AnnotationDescriptor<Annotation> repeatableDescriptor = descriptorRegistry.getRepeatableDescriptor( incomingUsageDescriptor );
		if ( repeatableDescriptor != null ) {
			// the incoming value is a usage of a "repeatable container", skip the registration
			return;
		}


		final List<AnnotationUsage<?>> registeredUsages;
		final List<AnnotationUsage<?>> existingRegisteredUsages = annotationUsageMap.get( incomingUsageDescriptor );
		if ( existingRegisteredUsages == null ) {
			registeredUsages = new ArrayList<>();
			annotationUsageMap.put( incomingUsageDescriptor, registeredUsages );
		}
		else {
			registeredUsages = existingRegisteredUsages;
		}

		registeredUsages.add( usage );
	}

	@Override
	public <A extends Annotation> List<AnnotationUsage<A>> getAllUsages(AnnotationDescriptor<A> annotationDescriptor) {
		final AnnotationDescriptor<Annotation> repeatableDescriptor = descriptorRegistry.getRepeatableDescriptor( annotationDescriptor );
		if ( repeatableDescriptor != null ) {
			throw new HibernateException( "Annotations which are repeatable-containers are not supported" );
		}

		//noinspection unchecked,rawtypes
		return (List) annotationUsageMap.get( annotationDescriptor );
	}

	@Override
	public <A extends Annotation> void forEachUsage(AnnotationDescriptor<A> annotationDescriptor, Consumer<AnnotationUsage<A>> consumer) {
		final List<AnnotationUsage<A>> allUsages = getAllUsages( annotationDescriptor );
		if ( CollectionHelper.isEmpty( allUsages ) ) {
			return;
		}
		allUsages.forEach( consumer );
	}
}
