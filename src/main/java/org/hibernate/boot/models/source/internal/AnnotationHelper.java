package org.hibernate.boot.models.source.internal;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.hibernate.boot.models.source.AnnotationAccessException;
import org.hibernate.boot.models.source.internal.reflection.ClassDetailsImpl;
import org.hibernate.boot.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationAttributeValue;
import org.hibernate.boot.models.source.spi.AnnotationDescriptor;
import org.hibernate.boot.models.source.spi.AnnotationDescriptorRegistry;
import org.hibernate.boot.models.source.spi.AnnotationTarget;
import org.hibernate.boot.models.source.spi.AnnotationUsage;
import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.source.spi.ClassDetailsRegistry;
import org.hibernate.boot.models.source.spi.HibernateAnnotations;
import org.hibernate.boot.models.source.spi.JpaAnnotations;
import org.hibernate.boot.models.spi.AnnotationProcessingContext;
import org.hibernate.internal.util.collections.CollectionHelper;

import static org.hibernate.internal.util.collections.CollectionHelper.arrayList;

/**
 * @author Steve Ebersole
 */
public class AnnotationHelper {
	public static void processAnnotations(
			Annotation[] annotations,
			AnnotationTarget target,
			BiConsumer<Class<? extends Annotation>,AnnotationUsage<?>> consumer,
			AnnotationProcessingContext processingContext) {
		final AnnotationDescriptorRegistry annotationDescriptorRegistry = processingContext.getAnnotationDescriptorRegistry();

		for ( int i = 0; i < annotations.length; i++ ) {
			final Annotation annotation = annotations[ i ];
			final Class<? extends Annotation> annotationType = annotation.annotationType();

			// skip a few well-know ones that are irrelevant
			if ( annotationType == Repeatable.class
					|| annotationType == Target.class
					|| annotationType == Retention.class
					|| annotationType == Documented.class ) {
				continue;
			}

			final AnnotationDescriptor<?> annotationDescriptor = annotationDescriptorRegistry.getDescriptor( annotationType );
			final AnnotationUsage<?> usage = makeUsage(
					annotation,
					annotationDescriptor,
					target,
					processingContext
			);
			consumer.accept( annotationType, usage );
		}
	}

	private static AnnotationUsage<?> makeUsage(
			Annotation annotation,
			AnnotationDescriptor<?> annotationDescriptor,
			AnnotationTarget target,
			AnnotationProcessingContext processingContext) {
		return new AnnotationUsageImpl( annotation, annotationDescriptor, target, processingContext );
	}

	public static <A extends Annotation> Map<String, AnnotationAttributeValue<?>> extractAttributeValues(
			A annotation,
			AnnotationDescriptor<A> annotationDescriptor,
			AnnotationProcessingContext processingContext) {
		if ( CollectionHelper.isEmpty( annotationDescriptor.getAttributes() ) ) {
			return Collections.emptyMap();
		}

		if ( annotationDescriptor.getAttributes().size() == 1 ) {
			final AnnotationAttributeDescriptor<A,?> attributeDescriptor = annotationDescriptor.getAttributes().get( 0 );
			//noinspection unchecked,rawtypes
			return Collections.singletonMap(
					attributeDescriptor.getAttributeName(),
					new AnnotationAttributeValueImpl( attributeDescriptor, extractAttributeValue( annotation, attributeDescriptor, processingContext ) )
			);
		}

		final Map<String, AnnotationAttributeValue<?>> valueMap = new HashMap<>();
		for ( int i = 0; i < annotationDescriptor.getAttributes().size(); i++ ) {
			final AnnotationAttributeDescriptor<A,?> attributeDescriptor = annotationDescriptor.getAttributes().get( i );
			//noinspection unchecked,rawtypes
			valueMap.put(
					attributeDescriptor.getAttributeName(),
					new AnnotationAttributeValueImpl( attributeDescriptor, extractAttributeValue( annotation, attributeDescriptor, processingContext ) )
			);
		}
		return valueMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <A extends Annotation, T> T extractAttributeValue(
			A annotation,
			AnnotationAttributeDescriptor<A,T> attributeDescriptor,
			AnnotationProcessingContext processingContext) {
		final AnnotationDescriptorRegistry annotationDescriptorRegistry = processingContext.getAnnotationDescriptorRegistry();
		final Object rawValue = extractRawAttributeValue( annotation, attributeDescriptor.getAttributeName() );

		if ( attributeDescriptor.getAttributeType().isAnnotation() ) {
			// the attribute type is an annotation. we want to wrap that in a usage.
			final AnnotationDescriptor<?> descriptor = annotationDescriptorRegistry.getDescriptor( (Class) attributeDescriptor.getAttributeType() );
			// todo (annotation-source) : target -
			//  	* null?
			//  	* the same target as the `annotation`?
			//		* the `annotation`?
			return (T) makeUsage( (Annotation) rawValue, descriptor, null, processingContext );
		}

		if ( attributeDescriptor.getAttributeType().equals( Class.class ) ) {
			assert rawValue instanceof Class;
			final Class<?> rawClassValue = (Class<?>) rawValue;
			return (T) resolveClassReference( processingContext, rawClassValue );
		}

		if ( attributeDescriptor.getAttributeType().isArray()
				&& attributeDescriptor.getAttributeType().getComponentType().isAnnotation() ) {
			// the attribute type is an array of annotations. we want to wrap those in a usage.  target?
			//noinspection unchecked
			final Class<? extends Annotation> annotationJavaType = (Class<? extends Annotation>) attributeDescriptor.getAttributeType().getComponentType();
			final AnnotationDescriptor<? extends Annotation> valuesAnnotationDescriptor = annotationDescriptorRegistry.getDescriptor( annotationJavaType );
			final Annotation[] rawValues = (Annotation[]) rawValue;
			final List<AnnotationUsage<?>> usages = arrayList( rawValues.length );
			for ( int i = 0; i < rawValues.length; i++ ) {
				final Annotation valueAnnotation = rawValues[i];
				usages.add( makeUsage( valueAnnotation, valuesAnnotationDescriptor, null, processingContext ) );
			}
			//noinspection unchecked
			return (T) usages;
		}

		if ( attributeDescriptor.getAttributeType().isArray()
				&& attributeDescriptor.getAttributeType().getComponentType().equals( Class.class ) ) {
			final Class<?>[] rawValues = (Class<?>[]) rawValue;
			final ClassDetails[] classDetails = new ClassDetails[ rawValues.length ];
			for ( int i = 0; i < rawValues.length; i++ ) {
				final Class<?> rawClassValue = rawValues[ i ];
				classDetails[i] = resolveClassReference( processingContext, rawClassValue );
			}
			//noinspection unchecked
			return (T) classDetails;
		}

		//noinspection unchecked
		return (T) rawValue;
	}

	public static <A extends Annotation, T> T extractRawAttributeValue(
			A annotation,
			String attributeName) {
		try {
			final Method method = annotation.getClass().getDeclaredMethod( attributeName );
			//noinspection unchecked
			return (T) method.invoke( annotation );
		}
		catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new AnnotationAccessException(
					String.format(
							Locale.ROOT,
							"Unable to extract attribute value : %s.%s",
							annotation.annotationType().getName(),
							attributeName
					),
					e
			);
		}
	}

	private static ClassDetails resolveClassReference(AnnotationProcessingContext processingContext, Class<?> rawClassValue) {
		final ClassDetailsRegistry classDetailsRegistry = processingContext.getClassDetailsRegistry();
		final ClassDetails existing = classDetailsRegistry.findManagedClass( rawClassValue.getName() );
		if ( existing != null ) {
			return existing;
		}
		else {
			final ClassDetails classDetails = new ClassDetailsImpl( rawClassValue, processingContext );
			classDetailsRegistry.addManagedClass( classDetails );
			return classDetails;
		}
	}

	public static <A extends Annotation> AnnotationUsage<A> getAnnotation(
			AnnotationDescriptor<A> type,
			Map<Class<? extends Annotation>,AnnotationUsage<? extends Annotation>> usageMap) {
		//noinspection unchecked
		return (AnnotationUsage<A>) usageMap.get( type.getAnnotationType() );
	}

	private AnnotationHelper() {
		// disallow direct instantiation
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

	public static <A extends Annotation> AnnotationUsage<A> getNamedAnnotation(AnnotationDescriptor<A> type, String matchValue, String attributeToMatch, Map<Class<? extends Annotation>, AnnotationUsage<?>> usageMap) {
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
				final AnnotationAttributeValue<List<AnnotationUsage<A>>> attributeValue = containerUsage.getAttributeValue( "value" );
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
		final AnnotationAttributeValue<String> attributeValue = annotationUsage.getAttributeValue( attributeToMatch );
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
}
