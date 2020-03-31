/*
 * Copyright 2019-2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.elipcero.carisa.administration.service.support;

import org.elipcero.carisa.administration.domain.DynamicObjectInstance;
import org.elipcero.carisa.administration.domain.support.ManyRelation;
import org.elipcero.carisa.core.data.EntityDataState;
import org.elipcero.carisa.core.data.Relation;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Operations for entity with relation.
 * @see DynamicObjectInstance
 *
 * @author David Suárez
 */

public interface MultiplyDependencyRelationService<T extends Relation, TRelation extends ManyRelation> {

    /**
     * Getting the entity
     * @param id the entity identifier
     * @return the entity
     */
    Mono<T> getById(final UUID id);

    /**
     * Create the entity and insert it into the parent. It's not done in the same transaction.
     * @param entity the entity to insert
     * @param manyRelation the relation between parent and entity
     * @return the inserted entity
     */
    Mono<T> create(final T entity, final TRelation manyRelation);

    /**
     * Update or create the entity. If the id exits is updated
     * otherwise is created. The identifier can not be updated.
     * @see MultiplyDependencyRelationService#create(Relation, ManyRelation)
     * @param id the dynamicn object identifier
     * @param entity the entity to insert or update
     * @param manyRelation the relation between parent and dynamic object
     * @return the inserted or updated dynamic object
     */
    Mono<EntityDataState<T>> updateOrCreate(final UUID id, final T entity, final TRelation manyRelation);
}
