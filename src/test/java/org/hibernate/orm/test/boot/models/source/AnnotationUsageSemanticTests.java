/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.orm.test.boot.models.source;

import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.boot.models.source.internal.reflection.ClassDetailsBuilderImpl;
import org.hibernate.boot.models.source.spi.AnnotationDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationDescriptorRegistry;
import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.source.spi.ClassDetailsRegistry;
import org.hibernate.boot.models.spi.ModelProcessingContext;
import org.hibernate.orm.test.boot.models.CustomAnnotation;
import org.hibernate.orm.test.boot.models.CustomAnnotations;
import org.hibernate.orm.test.boot.models.CustomMetaAnnotation;
import org.hibernate.orm.test.boot.models.ModelHelper;

import org.hibernate.testing.orm.junit.ServiceRegistry;
import org.hibernate.testing.orm.junit.ServiceRegistryScope;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that accessing annotation (usages) works consistently between
 * normal {@link java.lang.reflect.AnnotatedElement}, HCANN and
 * {@link org.hibernate.boot.models.source.spi.AnnotationTarget} sources
 *
 * @author Steve Ebersole
 */
public class AnnotationUsageSemanticTests {

	/**
	 * Assertions based on direct {@link java.lang.reflect.AnnotatedElement} apis
	 * so that we can define a baseline for assertions related to the various
	 * metamodels
	 */
	@Test
	void reflectionAssertions() {
		// meta-annotations
		assert !Whatever.class.isAnnotationPresent( CustomMetaAnnotation.class );
		assert !Something.class.isAnnotationPresent( CustomMetaAnnotation.class );
		assert !SomethingExtra.class.isAnnotationPresent( CustomMetaAnnotation.class );

		// inherited
		assert Whatever.class.isAnnotationPresent( CustomAnnotation.class );
		assert !Whatever.class.isAnnotationPresent( CustomAnnotations.class );
		assert Something.class.isAnnotationPresent( CustomAnnotation.class );
		assert !Something.class.isAnnotationPresent( CustomAnnotations.class );
		assert SomethingExtra.class.isAnnotationPresent( CustomAnnotation.class );
		assert SomethingExtra.class.isAnnotationPresent( CustomAnnotations.class );

		// shows the baseline for `AnnotationTarget#getRepeatedAnnotations`
		assertThat( SomethingExtra.class.getAnnotationsByType( CustomAnnotation.class ) ).hasSize( 1 );
		assertThat( SomethingExtra.class.getAnnotationsByType( CustomAnnotations.class ) ).hasSize( 1 );
	}

	@Test
	void hcannAssertions() {
		final JavaReflectionManager hcannReflectionManager = new JavaReflectionManager();
		final XClass whateverClass = hcannReflectionManager.toXClass( Whatever.class );
		final XClass somethingClass = hcannReflectionManager.toXClass( Something.class );
		final XClass somethingExtraClass = hcannReflectionManager.toXClass( SomethingExtra.class );

		// meta-annotations
		assert !whateverClass.isAnnotationPresent( CustomMetaAnnotation.class );
		assert !somethingClass.isAnnotationPresent( CustomMetaAnnotation.class );
		assert !somethingExtraClass.isAnnotationPresent( CustomMetaAnnotation.class );

		// inherited
		assert whateverClass.isAnnotationPresent( CustomAnnotation.class );
		assert !whateverClass.isAnnotationPresent( CustomAnnotations.class );
		assert somethingClass.isAnnotationPresent( CustomAnnotation.class );
		assert !somethingClass.isAnnotationPresent( CustomAnnotations.class );
		assert somethingExtraClass.isAnnotationPresent( CustomAnnotation.class );
		assert somethingExtraClass.isAnnotationPresent( CustomAnnotations.class );
	}

	@Test
	@ServiceRegistry
	void hcannSourceModelAssertions(ServiceRegistryScope scope) {
		final ModelProcessingContext processingContext = ModelHelper.buildProcessingContext( scope.getRegistry() );
		final AnnotationDescriptorRegistry annotationDescriptorRegistry = processingContext.getAnnotationDescriptorRegistry();
		final ClassDetailsRegistry classDetailsRegistry = processingContext.getClassDetailsRegistry();

		final AnnotationDescriptor<CustomMetaAnnotation> customMetaAnnotation = annotationDescriptorRegistry.getDescriptor( CustomMetaAnnotation.class );
		final AnnotationDescriptor<CustomAnnotation> customAnnotation = annotationDescriptorRegistry.getDescriptor( CustomAnnotation.class );
		final AnnotationDescriptor<CustomAnnotations> customsAnnotation = annotationDescriptorRegistry.getDescriptor( CustomAnnotations.class );

		final ClassDetails whateverClass = classDetailsRegistry.resolveClassDetails( Whatever.class.getName() );
		final ClassDetails somethingClass = classDetailsRegistry.resolveClassDetails( Something.class.getName() );
		final ClassDetails somethingExtraClass = classDetailsRegistry.resolveClassDetails( SomethingExtra.class.getName() );

		// meta-annotations
		assertThat( whateverClass.getAnnotation( customMetaAnnotation ) ).isNull();
		assertThat( somethingClass.getAnnotation( customMetaAnnotation ) ).isNull();
		assertThat( somethingExtraClass.getAnnotation( customMetaAnnotation ) ).isNull();

		// inherited
		assertThat( whateverClass.getAnnotation( customAnnotation ) ).isNotNull();
		assertThat( whateverClass.getAnnotation( customsAnnotation ) ).isNull();
		assertThat( somethingClass.getAnnotation( customAnnotation ) ).isNotNull();
		assertThat( somethingClass.getAnnotation( customsAnnotation ) ).isNull();
		assertThat( somethingExtraClass.getAnnotation( customAnnotation ) ).isNotNull();
		assertThat( somethingExtraClass.getAnnotation( customsAnnotation ) ).isNotNull();
	}

	@Test
	@ServiceRegistry
	void reflectionSourceModelAssertions(ServiceRegistryScope scope) {
		final ModelProcessingContext processingContext = ModelHelper.buildProcessingContext( scope.getRegistry() );
		final AnnotationDescriptorRegistry annotationDescriptorRegistry = processingContext.getAnnotationDescriptorRegistry();
		final ClassDetailsRegistry classDetailsRegistry = processingContext.getClassDetailsRegistry();

		final AnnotationDescriptor<CustomMetaAnnotation> customMetaAnnotation = annotationDescriptorRegistry.getDescriptor( CustomMetaAnnotation.class );
		final AnnotationDescriptor<CustomAnnotation> customAnnotation = annotationDescriptorRegistry.getDescriptor( CustomAnnotation.class );
		final AnnotationDescriptor<CustomAnnotations> customsAnnotation = annotationDescriptorRegistry.getDescriptor( CustomAnnotations.class );

		final ClassDetails whateverClass = classDetailsRegistry.resolveClassDetails( Whatever.class.getName(), ClassDetailsBuilderImpl.INSTANCE );
		final ClassDetails somethingClass = classDetailsRegistry.resolveClassDetails( Something.class.getName(), ClassDetailsBuilderImpl.INSTANCE );
		final ClassDetails somethingExtraClass = classDetailsRegistry.resolveClassDetails( SomethingExtra.class.getName(), ClassDetailsBuilderImpl.INSTANCE );

		// meta-annotations
		assertThat( whateverClass.getAnnotation( customMetaAnnotation ) ).isNull();
		assertThat( somethingClass.getAnnotation( customMetaAnnotation ) ).isNull();
		assertThat( somethingExtraClass.getAnnotation( customMetaAnnotation ) ).isNull();

		// inherited
		assertThat( whateverClass.getAnnotation( customAnnotation ) ).isNotNull();
		assertThat( whateverClass.getAnnotation( customsAnnotation ) ).isNull();
		assertThat( somethingClass.getAnnotation( customAnnotation ) ).isNotNull();
		assertThat( somethingClass.getAnnotation( customsAnnotation ) ).isNull();
		assertThat( somethingExtraClass.getAnnotation( customAnnotation ) ).isNotNull();
		assertThat( somethingExtraClass.getAnnotation( customsAnnotation ) ).isNotNull();

		// Finds the one from `Whatever` as well as from `SomethingExtra`
		assertThat( somethingExtraClass.getRepeatedAnnotations( customAnnotation ) ).hasSize( 2 );
		assertThat( somethingExtraClass.getRepeatedAnnotations( customsAnnotation ) ).hasSize( 1 );
	}

	@CustomAnnotation
	public static class Whatever {}

	public static class Something extends Whatever {}

	@CustomAnnotations( @CustomAnnotation )
	public static class SomethingExtra extends Whatever {}

}
