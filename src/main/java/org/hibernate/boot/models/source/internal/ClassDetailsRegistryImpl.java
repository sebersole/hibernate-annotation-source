/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.hibernate.boot.models.source.internal.hcann.ClassDetailsBuilderImpl;
import org.hibernate.boot.models.source.UnknownManagedClassException;
import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.source.spi.ClassDetailsBuilder;
import org.hibernate.boot.models.source.spi.ClassDetailsRegistry;
import org.hibernate.boot.models.spi.ModelProcessingContext;

/**
 * Standard ClassDetailsRegistry implementation
 *
 * @author Steve Ebersole
 */
public class ClassDetailsRegistryImpl implements ClassDetailsRegistry {
	private final ClassDetailsBuilder fallbackClassDetailsBuilder;
	private final ModelProcessingContext context;

	private final Map<String, ClassDetails> managedClassMap = new ConcurrentHashMap<>();
	private final Map<String, List<ClassDetails>> subTypeManagedClassMap = new ConcurrentHashMap<>();

	public ClassDetailsRegistryImpl(ModelProcessingContext context) {
		this( new ClassDetailsBuilderImpl( context ), context );
	}

	public ClassDetailsRegistryImpl(ClassDetailsBuilder fallbackClassDetailsBuilder, ModelProcessingContext context) {
		this.context = context;
		this.fallbackClassDetailsBuilder = fallbackClassDetailsBuilder;
	}

	@Override public ClassDetails findClassDetails(String name) {
		return managedClassMap.get( name );
	}

	@Override public ClassDetails getClassDetails(String name) {
		final ClassDetails named = managedClassMap.get( name );
		if ( named == null ) {
			throw new UnknownManagedClassException( "Unknown managed class" );
		}
		return named;
	}

	@Override public void forEachClassDetails(Consumer<ClassDetails> consumer) {
		managedClassMap.values().forEach( consumer );
	}

	@Override public List<ClassDetails> getDirectSubTypes(String superTypeName) {
		return subTypeManagedClassMap.get( superTypeName );
	}

	@Override public void forEachDirectSubType(String superTypeName, Consumer<ClassDetails> consumer) {
		final List<ClassDetails> directSubTypes = getDirectSubTypes( superTypeName );
		if ( directSubTypes != null ) {
			directSubTypes.forEach( consumer );
		}
	}

	@Override public void addClassDetails(ClassDetails classDetails) {
		addClassDetails( classDetails.getClassName(), classDetails );
	}

	@Override public void addClassDetails(String name, ClassDetails classDetails) {
		managedClassMap.put( name, classDetails );

		if ( classDetails.getSuperType() != null ) {
			List<ClassDetails> subTypes = subTypeManagedClassMap.get( classDetails.getSuperType().getName() );
			if ( subTypes == null ) {
				subTypes = new ArrayList<>();
				subTypeManagedClassMap.put( classDetails.getSuperType().getName(), subTypes );
			}
			subTypes.add( classDetails );
		}
	}

	@Override public ClassDetails resolveClassDetails(String name) {
		return resolveClassDetails( name, fallbackClassDetailsBuilder );
	}

	@Override public ClassDetails resolveClassDetails(
			String name,
			ClassDetailsBuilder creator) {
		final ClassDetails existing = managedClassMap.get( name );
		if ( existing != null ) {
			return existing;
		}

		final ClassDetails created = creator.buildClassDetails( name, context );
		addClassDetails( name, created );
		return created;
	}

	@Override public ClassDetails resolveClassDetails(
			String name,
			Supplier<ClassDetails> creator) {
		final ClassDetails existing = managedClassMap.get( name );
		if ( existing != null ) {
			return existing;
		}

		final ClassDetails created = creator.get();
		addClassDetails( name, created );
		return created;
	}
}
