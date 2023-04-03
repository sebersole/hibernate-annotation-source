/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal.hcann;

import org.hibernate.annotations.common.reflection.XMethod;
import org.hibernate.boot.models.source.internal.LazyAnnotationTarget;
import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.source.spi.MethodDetails;
import org.hibernate.boot.models.spi.ModelProcessingContext;

import static org.hibernate.boot.models.source.internal.ModifierUtils.isPersistableMethod;

/**
 * @author Steve Ebersole
 */
public class MethodDetailsImpl extends LazyAnnotationTarget implements MethodDetails {
	private final XMethod xMethod;
	private final ClassDetails type;

	public MethodDetailsImpl(XMethod xMethod, ModelProcessingContext processingContext) {
		super( xMethod::getAnnotations, processingContext );
		this.xMethod = xMethod;
		this.type = processingContext.getClassDetailsRegistry().resolveClassDetails(
				xMethod.getType().getName(),
				() -> new ClassDetailsImpl( xMethod.getType(), processingContext )
		);
	}

	@Override
	public String getName() {
		return xMethod.getName();
	}

	@Override
	public ClassDetails getType() {
		return type;
	}

	@Override
	public boolean isPersistable() {
		// todo (annotation-source) : HCANN does not give access to arguments
//		if ( !xMethod.getArguments().isEmpty() ) {
//			return false;
//		}

		if ( "void".equals( type.getName() ) || "Void".equals( type.getName() ) ) {
			return false;
		}

		return isPersistableMethod( xMethod.getModifiers() );
	}
}
