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

import org.elipcero.carisa.administration.domain.DynamicObjectPrototype;
import org.elipcero.carisa.administration.domain.support.ManyRelation;
import org.elipcero.carisa.core.data.EntityDataState;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Operations for dynamic object prototype.
 * @see DynamicObjectPrototype
 *
 * @author David Suárez
 */

public interface DynamicObjectPrototypeService<TRelation extends ManyRelation> {

    /**
     * Getting the dynamic object
     * @param id the object identifier
     * @return dynamic object
     */
    Mono<DynamicObjectPrototype> getById(final UUID id);

    /**
     * Create the dynamic object and insert it into the parent. It's not done in the same transaction.
     * @param dynamicObject the dynamic object to insert
     * @param manyRelation the relation between parent and dynamic object
     * @return the inserted dynamic object
     */
    Mono<DynamicObjectPrototype> create(
            final DynamicObjectPrototype dynamicObject, final TRelation manyRelation);

    /**
     * Update or create the dynamic object. If the id exits is updated
     * otherwise is created. The identifier can not be updated.
     * To create @see create
     * @param id the dynamicn object identifier
     * @param dynamicObject the dynamic object to insert or update
     * @param manyRelation the relation between parent and dynamic object
     * @return the inserted or updated dynamic object
     */
    Mono<EntityDataState<DynamicObjectPrototype>> updateOrCreate(
            final UUID id, final DynamicObjectPrototype dynamicObject, final TRelation manyRelation);
}
