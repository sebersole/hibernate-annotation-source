/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

/**
 * @author Steve Ebersole
 */
@GenericGenerator( name = "test_sequence", type = SequenceStyleGenerator.class )
package org.hibernate.orm.test.boot.models.bind;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
