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
import org.elipcero.carisa.administration.domain.DynamicObjectPrototype;
import org.elipcero.carisa.administration.repository.DynamicObjectPrototypeRepository;
import org.elipcero.carisa.core.data.Entity;
import org.elipcero.carisa.core.data.ManyRelation;
import org.elipcero.carisa.core.data.Relation;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelation;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelationService;

/**
 * Service for operations of the dynamic object prototype
 * @see MultiplyDependencyRelationService
 *
 * @author David Suárez
 */
public abstract class DynamicObjectPrototypeService<TRelation extends ManyRelation>
        extends MultiplyDependencyRelationService<DynamicObjectPrototype, TRelation> {

    public DynamicObjectPrototypeService(
            @NonNull DynamicObjectPrototypeRepository dynamicObjectPrototypeRepository,
            @NonNull MultiplyDependencyRelation<? extends Entity, DynamicObjectPrototype, TRelation> relation) {

        super(dynamicObjectPrototypeRepository, relation);
    }

    /**
     * @see MultiplyDependencyRelationService#updateEntity(Relation, Relation)
     */
    @Override
    protected void updateEntity(DynamicObjectPrototype entityForUpdating, DynamicObjectPrototype entity) {
        entityForUpdating.setName(entity.getName());
        entityForUpdating.setDescription(entity.getDescription());
    }
}
