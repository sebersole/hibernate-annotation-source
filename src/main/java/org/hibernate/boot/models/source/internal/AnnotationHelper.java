/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import org.hibernate.boot.models.source.AnnotationAccessException;

/**
 * Helper for dealing with actual {@link Annotation} references
 *
 * @author Steve Ebersole
 */
public class AnnotationHelper {

	/**
	 * Extract a "raw" value directly from an {@linkplain Annotation annotation}
	 */
	public static <A extends Annotation, T> T extractRawAttributeValue(A annotation, String attributeName) {
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

	public static <A extends Annotation> boolean isInherited(Class<A> annotationType) {
		return annotationType.isAnnotationPresent( Inherited.class );
	}

	private AnnotationHelper() {
		// disallow direct instantiation
	}
}
