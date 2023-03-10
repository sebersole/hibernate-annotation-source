/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal.hcann;

import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.source.spi.ClassDetailsBuilder;
import org.hibernate.boot.models.spi.ModelProcessingContext;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;

/**
 * HCANN based ClassDetailsBuilder
 *
 * @author Steve Ebersole
 */
public class ClassDetailsBuilderImpl implements ClassDetailsBuilder {
	private final ClassLoaderService classLoaderService;
	private final ReflectionManager hcannReflectionManager;

	public ClassDetailsBuilderImpl(ModelProcessingContext processingContext) {
		this.classLoaderService = processingContext.getMetadataBuildingContext()
				.getBootstrapContext()
				.getServiceRegistry()
				.getService( ClassLoaderService.class );
		this.hcannReflectionManager = processingContext.getMetadataBuildingContext()
				.getBootstrapContext()
				.getReflectionManager();
	}

	@Override
	public ClassDetails buildClassDetails(String name, ModelProcessingContext processingContext) {
		final Class<?> classForName = classLoaderService.classForName( name );
		final XClass xClassForName = hcannReflectionManager.toXClass( classForName );
		return new ClassDetailsImpl( xClassForName, processingContext );
	}
}
