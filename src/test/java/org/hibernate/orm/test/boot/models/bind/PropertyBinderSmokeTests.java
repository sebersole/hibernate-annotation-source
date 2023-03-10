/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.orm.test.boot.models.bind;

import java.util.Set;

import org.hibernate.boot.models.bind.internal.PropertyBinder;
import org.hibernate.boot.models.bind.internal.TypeBinder;
import org.hibernate.orm.test.boot.models.ModelHelper;
import org.hibernate.orm.test.boot.models.SimpleEntity;
import org.hibernate.boot.models.intermediate.spi.EntityHierarchy;
import org.hibernate.boot.models.intermediate.spi.EntityTypeMetadata;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;

import org.hibernate.testing.orm.junit.ServiceRegistry;
import org.hibernate.testing.orm.junit.ServiceRegistryScope;
import org.junit.jupiter.api.Test;

import static org.hibernate.boot.models.bind.internal.PropertyBinder.buildProperty;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple tests of {@link PropertyBinder} during development
 *
 * @author Steve Ebersole
 */
@ServiceRegistry
public class PropertyBinderSmokeTests {
	@Test
	void simpleTest(ServiceRegistryScope scope) {
		final Set<EntityHierarchy> entityHierarchies = ModelHelper.buildHierarchies( scope.getRegistry(), SimpleEntity.class );
		final EntityHierarchy hierarchy = entityHierarchies.iterator().next();
		final EntityTypeMetadata entity = hierarchy.getRoot();

		final RootClass entityBinding = (RootClass) TypeBinder.buildPersistentClass( entity );
		verifySimpleColumnEntityDetails( entityBinding );

		entity.forEachAttribute( (index, attribute) -> {
			if ( "id".equals( attribute.getName() ) ) {
				return;
			}

			final Property property = buildProperty( attribute, entity, entityBinding::getTable, entityBinding::getTable );
			verifySimpleEntityProperty( entityBinding, property );
		} );
	}

	public static void verifySimpleEntityProperty(RootClass entityBinding, Property property) {
		assertThat( property.getName() ).isNotNull();

		final BasicValue valueMapping = (BasicValue) property.getValue();
		assertThat( valueMapping ).isNotNull();

		if ( "name".equals( property.getName() ) ) {
			assertThat( valueMapping.getColumn() ).isNotNull();
			assertThat( valueMapping.getColumn().getText() ).isEqualTo( "description" );
			assertThat( valueMapping.getTable() ).isEqualTo( entityBinding.getTable() );
		}
		else if ( "name2".equals( property.getName() ) ) {
			assertThat( valueMapping.getColumn() ).isNotNull();
			assertThat( valueMapping.getColumn().getText() ).isEqualTo( "name2" );
			assertThat( valueMapping.getTable().getName() ).isEqualTo( "another_table" );
			assertThat( valueMapping.getTable() ).isSameAs( entityBinding.getTable( "another_table" ) );
		}
		else {
			throw new RuntimeException( "Unexpected Property : " + property );
		}
	}

	public static void verifySimpleColumnEntityDetails(RootClass entityBinding) {
		assertThat( entityBinding ).isNotNull();
		assertThat( entityBinding.getTable() ).isNotNull();
		assertThat( entityBinding.getTable().getName() ).isEqualTo( "simple_entities" );
		assertThat( entityBinding.getJoins() ).hasSize( 1 );
		assertThat( entityBinding.getJoins().get(0).getTable().getName() ).isEqualTo( "another_table" );
	}
}
