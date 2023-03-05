/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.boot.models.intermediate.spi;

import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.boot.models.source.internal.AnnotationWrapperHelper;
import org.hibernate.boot.models.source.spi.AnnotationUsage;

/**
 * Details about caching related to the natural-id of an entity
 *
 * @see Caching
 *
 * @author Steve Ebersole
 */
public class NaturalIdCaching {
	private boolean enabled;
	private String region;

	public NaturalIdCaching(boolean enabled) {
		this.enabled = enabled;
	}

	public NaturalIdCaching(AnnotationUsage<NaturalIdCache> cacheAnnotation, Caching caching) {
		this.enabled = cacheAnnotation != null;

		if ( enabled ) {
			this.region = AnnotationWrapperHelper.extractValue(
					cacheAnnotation,
					"region",
					caching.getRegion() + "##NaturalId"
			);
		}
		else {
			// use the default value
			this.region = caching.getRegion() + "##NaturalId";
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getRegion() {
		return region;
	}
}
