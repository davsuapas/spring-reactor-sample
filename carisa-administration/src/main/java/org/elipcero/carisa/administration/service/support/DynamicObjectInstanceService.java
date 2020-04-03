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
import org.elipcero.carisa.administration.domain.DynamicObjectInstance;
import org.elipcero.carisa.administration.repository.DynamicObjectInstanceRepository;
import org.elipcero.carisa.administration.repository.DynamicObjectPrototypeRepository;
import org.elipcero.carisa.core.data.Entity;
import org.elipcero.carisa.core.data.ManyRelation;
import org.elipcero.carisa.core.data.Relation;
import org.elipcero.carisa.core.reactive.data.DependencyRelationRefNotFoundException;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelation;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelationService;
import reactor.core.publisher.Mono;

/**
 * Service for operations of the dynamic object instance
 * @see MultiplyDependencyRelationService
 *
 * @author David Suárez
 */
public abstract class DynamicObjectInstanceService<TRelation extends ManyRelation>
        extends MultiplyDependencyRelationService<DynamicObjectInstance, TRelation> {

    private final DynamicObjectPrototypeRepository dynamicObjectPrototypeRepository;

    public DynamicObjectInstanceService(
            @NonNull DynamicObjectInstanceRepository dynamicObjectInstanceRepository,
            @NonNull MultiplyDependencyRelation<? extends Entity, DynamicObjectInstance, TRelation> relation,
            @NonNull DynamicObjectPrototypeRepository dynamicObjectPrototypeRepository) {

        super(dynamicObjectInstanceRepository, relation);

        this.dynamicObjectPrototypeRepository = dynamicObjectPrototypeRepository;
    }

    /**
     * @see MultiplyDependencyRelationService#create(Relation, ManyRelation)
     * Also check that the prototypeId exists
     */
    @Override
    public Mono<DynamicObjectInstance> create(final DynamicObjectInstance entity, final TRelation manyRelation) {
        return this.dynamicObjectPrototypeRepository.existsById(entity.getPrototypeId())
                .flatMap(exsits -> {
                    if (exsits) {
                        return super.create(entity, manyRelation);
                    }
                    return Mono.error(new DependencyRelationRefNotFoundException(
                            String.format("The object prototype with ID: %s not found", entity.getPrototypeId())));
                });
    }

    /**
     * @see MultiplyDependencyRelationService#updateEntity(Relation, Relation)
     */
    @Override
    protected void updateEntity(DynamicObjectInstance entityForUpdating, DynamicObjectInstance entity) {
        entityForUpdating.setName(entity.getName());
        entityForUpdating.setDescription(entity.getDescription());
    }
}
