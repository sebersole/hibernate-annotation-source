/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.source.internal.dynamic;

import org.hibernate.boot.models.source.spi.ClassDetails;
import org.hibernate.boot.models.source.spi.MethodDetails;
import org.hibernate.boot.models.spi.ModelProcessingContext;

/**
 *
 * @author Steve Ebersole
 */
public class MethodDetailsImpl extends AbstractDynamicAnnotationTarget implements DynamicMemberDetails, MethodDetails {
	private final String name;
	private final String attributeName;
	private ClassDetails type;

	public MethodDetailsImpl(
			String name,
			String attributeName,
			ModelProcessingContext processingContext) {
		super( processingContext );
		this.name = name;
		this.attributeName = attributeName;
	}

	public MethodDetailsImpl(
			String name,
			ModelProcessingContext processingContext) {
		this( name, null, processingContext );
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String resolveAttributeName() {
		return attributeName;
	}

	@Override
	public ClassDetails getType() {
		return type;
	}

	public void setType(ClassDetails type) {
		this.type = type;
	}

	@Override
	public boolean isPersistable() {
		return attributeName != null;
	}
}
