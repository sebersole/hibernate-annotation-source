/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.spi;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.hibernate.boot.models.source.UnknownManagedClassException;

/**
 * Registry of all known {@link ClassDetails} references
 *
 * @author Steve Ebersole
 */
public interface ClassDetailsRegistry {
	/**
	 * Find the managed-class with the given {@code name}, if there is one.
	 * Returns {@code null} if there are none registered with that name.
	 */
	ClassDetails findClassDetails(String name);

	/**
	 * Form of {@link #findClassDetails} throwing an exception if no registration is found
	 *
	 * @throws UnknownManagedClassException If no registration is found with the given {@code name}
	 */
	ClassDetails getClassDetails(String name);

	/**
	 * Visit each registered managed-class
	 */
	void forEachClassDetails(Consumer<ClassDetails> consumer);

	/**
	 * Get the list of all direct subtypes for the named managed-class.  Returns
	 * {@code null} if there are none
	 */
	List<ClassDetails> getDirectSubTypes(String superTypeName);

	/**
	 * Visit each direct subtype of the named managed-class
	 */
	void forEachDirectSubType(String superTypeName, Consumer<ClassDetails> consumer);

	/**
	 * Adds a managed-class descriptor using its {@linkplain ClassDetails#getName() name}
	 * as the registration key.
	 */
	void addClassDetails(ClassDetails classDetails);

	/**
	 * Adds a managed-class descriptor using the given {@code name} as the registration key
	 */
	void addClassDetails(String name, ClassDetails classDetails);

	/**
	 * Resolves a managed-class by name.  If there is currently no such registration,
	 * one is created.
	 */
	ClassDetails resolveClassDetails(String name);

	/**
	 * Resolves a managed-class by name.  If there is currently no such registration,
	 * one is created using the specified {@code creator}.
	 */
	ClassDetails resolveClassDetails(String name, ClassDetailsBuilder creator);

	/**
	 * Resolves a managed-class by name.  If there is currently no such registration,
	 * one is created using the specified {@code creator}.
	 */
	ClassDetails resolveClassDetails(String name, Supplier<ClassDetails> creator);
}
