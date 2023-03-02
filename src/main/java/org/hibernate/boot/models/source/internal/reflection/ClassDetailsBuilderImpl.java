/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal.reflection;

import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.source.spi.ClassDetailsBuilder;
import org.hibernate.boot.models.spi.AnnotationProcessingContext;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;

/**
 * ClassDetailsBuilder implementation based on {@link Class}
 *
 * @author Steve Ebersole
 */
public class ClassDetailsBuilderImpl implements ClassDetailsBuilder {
	/**
	 * Singleton access
	 */
	public static final ClassDetailsBuilderImpl INSTANCE = new ClassDetailsBuilderImpl();

	@Override
	public ClassDetails buildClassDetails(String name, AnnotationProcessingContext processingContext) {
		return buildClassDetailsStatic( name, processingContext );
	}

	public static ClassDetails buildClassDetailsStatic(String name, AnnotationProcessingContext processingContext) {
		return buildClassDetails(
				processingContext.getMetadataBuildingContext()
						.getBootstrapContext()
						.getServiceRegistry()
						.getService( ClassLoaderService.class )
						.classForName( name ),
				processingContext
		);
	}

	public static ClassDetails buildClassDetails(Class<?> javaClass, AnnotationProcessingContext processingContext) {
		return new ClassDetailsImpl( javaClass, processingContext );
	}
}
