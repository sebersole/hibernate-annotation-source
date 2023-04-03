/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.spi;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.boot.models.source.AnnotationAccessException;

/**
 * @author Steve Ebersole
 */
public interface AnnotationTarget {
	/**
	 * The kind of target
	 */
	Kind getKind();

	String getName();

	/**
	 * Get the use of the given annotation on this target.
	 * <p/>
	 * For {@linkplain Repeatable repeatable} annotation types,
	 * this method will return the usage if there is just one.  If there are multiple,
	 * {@link AnnotationAccessException} will be thrown
	 * <p/>
	 * For annotations which can {@linkplain java.lang.annotation.ElementType#ANNOTATION_TYPE target annotations},
	 * all annotations on this target will be checked as well.
	 *
	 * @return The usage or {@code null}
	 */
	<A extends Annotation> AnnotationUsage<A> getAnnotation(AnnotationDescriptor<A> type);

	/**
	 * For {@linkplain Repeatable repeatable} annotation types, this method will return a list
	 * of all the annotations on the target, even those on the {@linkplain Repeatable#value() "containing annotation"}.
	 * For example, calling this method with {@link org.hibernate.annotations.JavaTypeRegistration}
	 * will result in either<ul>
	 *     <li>if just one {@link org.hibernate.annotations.JavaTypeRegistration}, it is returned in a singleton list of</li>
	 *     <li>if just one {@link org.hibernate.annotations.JavaTypeRegistrations}, all of its values are returned as a list</li>
	 *     <li>if we have both, all of the values are combined</li>
	 *     <li>otherwise, an empty list</li>
	 * </ul>
	 * <p/>
	 * For all other cases, this method returns the result of {@link #getAnnotation} as an empty or singleton list
	 */
	<A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotations(AnnotationDescriptor<A> type);

	/**
	 * Call the {@code consumer} for each {@linkplain AnnotationUsage usage} of the
	 * given {@code type}.
	 *
	 * @apiNote For {@linkplain Repeatable repeatable} annotation types, the consumer will also be
	 * called for those defined on the container.
	 */
	<X extends Annotation> void forEachAnnotation(AnnotationDescriptor<X> type, Consumer<AnnotationUsage<X>> consumer);

	/**
	 * Get a usage of the given annotation {@code type} whose {@code attributeToMatch} attribute value
	 * matches the given {@code matchName}.
	 *
	 * @param matchName The name to match.
	 */
	default <X extends Annotation> AnnotationUsage<X> getNamedAnnotation(
			AnnotationDescriptor<X> type,
			String matchName) {
		return getNamedAnnotation( type, matchName, "name" );
	}



	/**
	 * Get a usage of the given annotation {@code type} whose {@code attributeToMatch} attribute value
	 * matches the given {@code matchName}.
	 *
	 * @param matchName The name to match.
	 * @param attributeToMatch Name of the attribute to match on.
	 */
	<X extends Annotation> AnnotationUsage<X> getNamedAnnotation(
			AnnotationDescriptor<X> type,
			String matchName,
			String attributeToMatch);


	/**
	 * Subset of {@linkplain java.lang.annotation.ElementType annotation targets} supported for mapping annotations
	 */
	enum Kind {
		ANNOTATION( ElementType.ANNOTATION_TYPE ),
		CLASS( ElementType.TYPE ),
		FIELD( ElementType.FIELD ),
		METHOD( ElementType.METHOD ),
		PACKAGE( ElementType.PACKAGE );

		private final ElementType elementType;

		Kind(ElementType elementType) {
			this.elementType = elementType;
		}

		public ElementType getCorrespondingElementType() {
			return elementType;
		}

		public static EnumSet<Kind> from(Target target) {
			if ( target == null ) {
				return EnumSet.allOf( Kind.class );
			}
			return from( target.value() );
		}

		public static EnumSet<Kind> from(ElementType[] elementTypes) {
			final EnumSet<Kind> kinds = EnumSet.noneOf( Kind.class );
			final Kind[] values = values();
			for ( int i = 0; i < elementTypes.length; i++ ) {
				for ( int v = 0; v < values.length; v++ ) {
					if ( values[v].getCorrespondingElementType().equals( elementTypes[i] ) ) {
						kinds.add( values[v] );
					}
				}
			}
			return kinds;
		}

		public static Kind from(ElementType elementType) {
			final Kind[] values = values();
			for ( int i = 0; i < values.length; i++ ) {
				if ( values[i].getCorrespondingElementType().equals( elementType ) ) {
					return values[i];
				}
			}
			return null;
		}
	}
}
