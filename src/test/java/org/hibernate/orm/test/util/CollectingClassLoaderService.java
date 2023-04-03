/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */

package org.hibernate.orm.test.util;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;

/**
 * @author Steve Ebersole
 */
public class CollectingClassLoaderService implements ClassLoaderService {
	private final ClassLoaderService realClassLoaderService;
	private final Map<String,Class<?>> classMap;

	public CollectingClassLoaderService() {
		this.realClassLoaderService = new ClassLoaderServiceImpl();
		this.classMap = new HashMap<>();
	}

	public <T> Class<T> getCachedClassForName(String className) {
		//noinspection unchecked
		return (Class<T>) classMap.get( className );
	}

	@Override
	public <T> Class<T> classForName(String className) {
		final Class<?> existing = classMap.get( className );
		if ( existing != null ) {
			if ( existing == Void.class ) {
				throw new ClassLoadingException( "Unknown class - `" + className + "`" );
			}
			//noinspection unchecked
			return (Class<T>) existing;
		}

		try {
			final Class<T> loaded = realClassLoaderService.classForName( className );
			classMap.put( className, loaded );
			return loaded;
		}
		catch (ClassLoadingException e) {
			classMap.put( className, Void.class );
			throw e;
		}
	}

	@Override
	public URL locateResource(String name) {
		return realClassLoaderService.locateResource( name );
	}

	@Override
	public InputStream locateResourceStream(String name) {
		return realClassLoaderService.locateResourceStream( name );
	}

	@Override
	public List<URL> locateResources(String name) {
		return realClassLoaderService.locateResources( name );
	}

	@Override
	public <S> Collection<S> loadJavaServices(Class<S> serviceContract) {
		return realClassLoaderService.loadJavaServices( serviceContract );
	}

	@Override
	public <T> T generateProxy(InvocationHandler handler, Class... interfaces) {
		return realClassLoaderService.generateProxy( handler, interfaces );
	}

	@Override
	public Package packageForNameOrNull(String packageName) {
		return realClassLoaderService.packageForNameOrNull( packageName );
	}

	@Override
	public <T> T workWithClassLoader(Work<T> work) {
		return realClassLoaderService.workWithClassLoader( work );
	}

	@Override
	public void stop() {
		classMap.clear();
		realClassLoaderService.stop();
	}
}
