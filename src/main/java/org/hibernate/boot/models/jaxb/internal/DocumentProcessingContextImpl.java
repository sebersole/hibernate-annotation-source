/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.jaxb.internal;

import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.mapping.JaxbEntityMappings;
import org.hibernate.boot.models.jaxb.spi.DocumentProcessingContext;
import org.hibernate.boot.models.spi.ModelProcessingContext;

/**
 * @author Steve Ebersole
 */
public class DocumentProcessingContextImpl implements DocumentProcessingContext {
	private final JaxbEntityMappings xmlMapping;
	private final Origin xmlOrigin;
	private final ModelProcessingContext rootContext;

	public DocumentProcessingContextImpl(
			JaxbEntityMappings xmlMapping,
			Origin xmlOrigin,
			ModelProcessingContext rootContext) {
		this.xmlMapping = xmlMapping;
		this.xmlOrigin = xmlOrigin;
		this.rootContext = rootContext;
	}

	@Override
	public JaxbEntityMappings getXmlMapping() {
		return xmlMapping;
	}

	@Override
	public Origin getXmlOrigin() {
		return xmlOrigin;
	}

	@Override
	public ModelProcessingContext getModelProcessingContext() {
		return rootContext;
	}
}
