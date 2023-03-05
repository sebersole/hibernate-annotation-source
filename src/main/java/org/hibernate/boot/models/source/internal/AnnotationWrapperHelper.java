/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.hibernate.boot.models.intermediate.spi.IdentifiableTypeMetadata;
import org.hibernate.boot.models.source.AnnotationAccessException;
import org.hibernate.boot.models.source.spi.AnnotationAttributeValue;
import org.hibernate.boot.models.source.spi.AnnotationDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationUsage;
import org.hibernate.boot.models.source.spi.HibernateAnnotations;
import org.hibernate.boot.models.source.spi.JpaAnnotations;

/**
 * Helper for dealing with annotation wrappers -<ul>
 *     <li>{@link org.hibernate.boot.models.source.spi.AnnotationDescriptor}</li>
 *     <li>{@link org.hibernate.boot.models.source.spi.AnnotationAttributeDescriptor}</li>
 *     <li>{@link org.hibernate.boot.models.source.spi.AnnotationUsage}</li>
 *     <li>{@link org.hibernate.boot.models.source.spi.AnnotationAttributeValue}</li>
 * </ul>
 *
 * @see AnnotationHelper
 * @see AnnotationDescriptorBuilder
 * @see AnnotationUsageBuilder
 *
 * @author Steve Ebersole
 */
public class AnnotationWrapperHelper {

	/**
	 * Get the {@link AnnotationUsage} from the {@code usageMap} for the given {@code type}
	 */
	public static <A extends Annotation> AnnotationUsage<A> getAnnotation(
			AnnotationDescriptor<A> type,
			Map<Class<? extends Annotation>,AnnotationUsage<? extends Annotation>> usageMap) {
		//noinspection unchecked
		return (AnnotationUsage<A>) usageMap.get( type.getAnnotationType() );
	}

	public static <A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotations(AnnotationDescriptor<A> type, Map<Class<? extends Annotation>, AnnotationUsage<?>> usageMap) {
		final AnnotationUsage<A> annotation = getAnnotation( type, usageMap );
		if ( annotation != null ) {
			return Collections.singletonList( annotation );
		}

		final AnnotationDescriptor<?> repeatableContainer = type.getRepeatableContainer();
		if ( repeatableContainer != null ) {
			final AnnotationUsage<?> containerUsage = getAnnotation( repeatableContainer, usageMap );
			if ( containerUsage != null ) {
				//noinspection unchecked
				return (List<AnnotationUsage<A>>) containerUsage.getAttributeValue( "value" ).getValue();
			}
		}

		return Collections.emptyList();
	}

	public static <A extends Annotation> AnnotationUsage<A> getNamedAnnotation(
			AnnotationDescriptor<A> type,
			String matchValue,
			String attributeToMatch,
			Map<Class<? extends Annotation>, AnnotationUsage<?>> usageMap) {
		final AnnotationUsage<?> annotationUsage = usageMap.get( type.getAnnotationType() );
		if ( annotationUsage != null ) {
			if ( nameMatches( annotationUsage, matchValue, attributeToMatch ) ) {
				//noinspection unchecked
				return (AnnotationUsage<A>) annotationUsage;
			}
			return null;
		}

		final AnnotationDescriptor<?> containerType = type.getRepeatableContainer();
		if ( containerType != null ) {
			final AnnotationUsage<?> containerUsage = usageMap.get( containerType.getAnnotationType() );
			if ( containerUsage != null ) {
				final AnnotationAttributeValue<?,List<AnnotationUsage<A>>> attributeValue = containerUsage.getAttributeValue( "value" );
				if ( attributeValue != null ) {
					final List<AnnotationUsage<A>> repeatedUsages = attributeValue.getValue();
					for ( int i = 0; i < repeatedUsages.size(); i++ ) {
						final AnnotationUsage<A> repeatedUsage = repeatedUsages.get( i );
						if ( nameMatches( repeatedUsage, matchValue, attributeToMatch ) ) {
							return repeatedUsage;
						}
					}
				}
			}
		}

		return null;
	}

	private static boolean nameMatches(AnnotationUsage<?> annotationUsage, String matchValue, String attributeToMatch) {
		final AnnotationAttributeValue<String,String> attributeValue = annotationUsage.getAttributeValue( attributeToMatch );
		return attributeValue != null && matchValue.equals( attributeValue.getValue() );
	}

	public static void forEachOrmAnnotation(Consumer<AnnotationDescriptor<?>> consumer) {
		forEachOrmAnnotation( JpaAnnotations.class, consumer );
		forEachOrmAnnotation( HibernateAnnotations.class, consumer );
	}

	private static void forEachOrmAnnotation(Class<?> declarer, Consumer<AnnotationDescriptor<?>> consumer) {
		for ( Field field : declarer.getFields() ) {
			if ( AnnotationDescriptor.class.equals( field.getType() ) ) {
				try {
					consumer.accept( (AnnotationDescriptor<?>) field.get( null ) );
				}
				catch (IllegalAccessException e) {
					throw new AnnotationAccessException(
							String.format(
									Locale.ROOT,
									"Unable to access standard annotation descriptor field - %s",
									field.getName()
							),
							e
					);
				}
			}
		}
	}

	public static <V,W> AnnotationAttributeValue<V,W> extractValueWrapper(AnnotationUsage<?> usage, String attributeName) {
		if ( usage == null ) {
			return null;
		}
		return usage.getAttributeValue( attributeName );
	}

	public static <X> X extractValue(AnnotationUsage<?> usage, String attributeName) {
		if ( usage == null ) {
			return null;
		}

		final AnnotationAttributeValue<?,X> value = extractValueWrapper( usage, attributeName );
		if ( value == null ) {
			//noinspection unchecked
			return (X) usage.getAnnotationDescriptor().getAttribute( attributeName ).getAttributeDefault();
		}

		return value.getValue();
	}

	public static <X> X extractValue(AnnotationUsage<?> usage, String attributeName, X implicitValue) {
		if ( usage == null ) {
			return implicitValue;
		}

		final AnnotationAttributeValue<?,X> value = extractValueWrapper( usage, attributeName );
		if ( value == null ) {
			//noinspection unchecked
			return (X) usage.getAnnotationDescriptor().getAttribute( attributeName ).getAttributeDefault();
		}

		if ( value.isDefaultValue() ) {
			return implicitValue;
		}

		return value.getValue();
	}

	public static <X> X extractValue(AnnotationUsage<?> usage, String attributeName, Supplier<X> defaultValueSupplier) {
		return extractValue( extractValueWrapper( usage, attributeName ), defaultValueSupplier );
	}

	public static <X> X extractValue(AnnotationAttributeValue<?,X> attributeValue, Supplier<X> defaultValueSupplier) {
		if ( attributeValue == null || attributeValue.isDefaultValue() ) {
			return defaultValueSupplier.get();
		}
		return attributeValue.getValue();
	}

	/**
	 * Look for the given annotation on the passed type as well as any of its super-types.
	 * Similar concept as {@link java.lang.annotation.Inherited}, but without needing the
	 * physical {@link java.lang.annotation.Inherited} meta-annotation.
	 * <p/>
	 * This is useful for JPA annotations which can be used in inheritance situations but
	 * are never defined with {@link java.lang.annotation.Inherited}
	 */
	public static <A extends Annotation> AnnotationUsage<A> findInheritedAnnotation(
			IdentifiableTypeMetadata base,
			AnnotationDescriptor<A> annotationDescriptor) {
		final AnnotationUsage<A> annotation = base.getManagedClass().getAnnotation( annotationDescriptor );
		if ( annotation != null ) {
			return annotation;
		}

		if ( ! annotationDescriptor.isInherited() ) {
			// we have a case where the annotation is not marked as `@Inherited` (like JPA's annotations),
			// so manually check up the hierarchy.
			// todo (annotation-source) : do we need to manually check meta-annotations?

			if ( base.getSuperType() != null ) {
				return findInheritedAnnotation( base.getSuperType(), annotationDescriptor );
			}
		}

		return null;
	}

	private AnnotationWrapperHelper() {
	}
}
