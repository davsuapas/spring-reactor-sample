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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.elipcero.carisa.administration.domain.DynamicObjectPrototype;
import org.elipcero.carisa.administration.domain.support.ManyRelation;
import org.elipcero.carisa.administration.repository.DynamicObjectPrototypeRepository;
import org.elipcero.carisa.core.data.Entity;
import org.elipcero.carisa.core.data.EntityDataState;
import org.elipcero.carisa.core.reactive.data.DependencyRelationCreateCommand;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelation;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @see DynamicObjectPrototypeService
 *
 * @author David Suárez
 */
@RequiredArgsConstructor
public abstract class DefaultDynamicObjectPrototypeService<TRelation extends ManyRelation>
        implements DynamicObjectPrototypeService<TRelation> {

    @NonNull
    private DynamicObjectPrototypeRepository dynamicObjectRepository;

    @NonNull
    private final MultiplyDependencyRelation<? extends Entity, DynamicObjectPrototype, TRelation> relation;

    /**
     * @see DynamicObjectPrototypeService
     */
    @Override
    public Mono<DynamicObjectPrototype> getById(final UUID id) {
        return this.dynamicObjectRepository.findById(id);
    }

    /**
     * @see DynamicObjectPrototypeService
     */
    @Override
    public Mono<DynamicObjectPrototype> create(
            final DynamicObjectPrototype dynamicObject, final TRelation manyRelation) {

        return this.relation.create(
                new DependencyRelationCreateCommand<DynamicObjectPrototype, TRelation>() {
                    @Override
                    public DynamicObjectPrototype getChild() {
                        return dynamicObject;
                    }

                    @Override
                    public TRelation getRelation() {
                        manyRelation.setParentId(dynamicObject.getParentId());
                        manyRelation.setChildId(dynamicObject.getId());
                        return manyRelation;
                    }
                });
    }

    /**
     * @see DynamicObjectPrototypeService
     */
    @Override
    public Mono<EntityDataState<DynamicObjectPrototype>> updateOrCreate(
            final UUID id, final DynamicObjectPrototype dynamicObject, final TRelation manyRelation) {

        return this.dynamicObjectRepository
                .updateCreate(id,
                        dynamicObjectForUpdating -> dynamicObjectForUpdating.setName(dynamicObject.getName()),
                        this.create(dynamicObject, manyRelation));
    }
}
