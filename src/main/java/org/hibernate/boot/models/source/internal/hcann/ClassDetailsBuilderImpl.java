/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal.hcann;

import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.boot.model.internal.JPAXMLOverriddenMetadataProvider;
import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.source.spi.ClassDetailsBuilder;
import org.hibernate.boot.models.spi.ModelProcessingContext;
import org.hibernate.boot.spi.ClassLoaderAccess;

/**
 * HCANN based ClassDetailsBuilder
 *
 * @author Steve Ebersole
 */
public class ClassDetailsBuilderImpl implements ClassDetailsBuilder {
	private final ClassLoaderAccess classLoaderAccess;
	private final ReflectionManager reflectionManager;

	public ClassDetailsBuilderImpl(ModelProcessingContext processingContext) {
		this.classLoaderAccess = processingContext.getClassLoaderAccess();
		this.reflectionManager = generateReflectionManager( classLoaderAccess, processingContext );
	}

	private static ReflectionManager generateReflectionManager(ClassLoaderAccess classLoaderAccess, ModelProcessingContext processingContext) {
		final JavaReflectionManager reflectionManager = new JavaReflectionManager();
		reflectionManager.setMetadataProvider( new JPAXMLOverriddenMetadataProvider(
				classLoaderAccess,
				processingContext.getMetadataBuildingContext().getBootstrapContext()
		) );
		return reflectionManager;
	}

	@Override
	public ClassDetails buildClassDetails(String name, ModelProcessingContext processingContext) {
		final Class<?> classForName = classLoaderAccess.classForName( name );
		final XClass xClassForName = reflectionManager.toXClass( classForName );
		return new ClassDetailsImpl( xClassForName, processingContext );
	}
}
