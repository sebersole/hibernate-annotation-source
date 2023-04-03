/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.orm.test.util;

import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.spi.ClassLoaderAccess;

import org.jboss.logging.Logger;

/**
 * Mostly a copy of {@link org.hibernate.boot.internal.ClassLoaderAccessImpl}, just to
 * "override" {@link #isSafeClass(String)}
 *
 * @author Steve Ebersole
 */
public class ClassLoaderAccessImpl implements ClassLoaderAccess {
	private static final Logger log = Logger.getLogger( ClassLoaderAccessImpl.class );

	private final ClassLoaderService classLoaderService;
	private final ClassLoader tempClassLoader;

	private final Set<String> classesLoadedFromTempClassLoader;

	public ClassLoaderAccessImpl(ClassLoader tempClassLoader, ClassLoaderService classLoaderService) {
		this.tempClassLoader = tempClassLoader;
		this.classLoaderService = classLoaderService;

		this.classesLoadedFromTempClassLoader = tempClassLoader == null
				? Collections.emptySet()
				: new HashSet<>();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<?> classForName(String name) {
		if ( name == null ) {
			throw new IllegalArgumentException( "Name of class to load cannot be null" );
		}

		if ( isSafeClass( name ) ) {
			return classLoaderService.classForName( name );
		}
		else {
			log.debugf( "Not known whether passed class name [%s] is safe", name );
			if ( tempClassLoader == null ) {
				log.debugf(
						"No temp ClassLoader provided; using live ClassLoader " +
								"for loading potentially unsafe class : %s",
						name
				);
				return classLoaderService.classForName( name );
			}
			else {
				log.debugf(
						"Temp ClassLoader was provided, so we will use that : %s",
						name
				);
				try {
					classesLoadedFromTempClassLoader.add( name );
					return tempClassLoader.loadClass( name );
				}
				catch (ClassNotFoundException e) {
					throw new ClassLoadingException( name );
				}
			}
		}
	}

	private boolean isSafeClass(String name) {
		// classes in any of these packages are safe to load through the "live" ClassLoader
		return name.startsWith( "java." )
				|| name.startsWith( "javax." )
				|| name.startsWith( "jakarta." )
				|| ( name.startsWith( "org.hibernate." ) && !name.contains( ".test." ) );

	}

	public ClassLoader getTempClassLoader() {
		return tempClassLoader;
	}

	public Set<String> getClassesLoadedFromTempClassLoader() {
		return classesLoadedFromTempClassLoader;
	}

	@Override
	public URL locateResource(String resourceName) {
		return classLoaderService.locateResource( resourceName );
	}
}
