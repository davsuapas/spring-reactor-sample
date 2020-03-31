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
import org.elipcero.carisa.administration.domain.support.ManyRelation;
import org.elipcero.carisa.administration.repository.DynamicObjectInstanceRepository;
import org.elipcero.carisa.core.data.Entity;
import org.elipcero.carisa.core.data.Relation;
import org.elipcero.carisa.core.reactive.data.MultiplyDependencyRelation;

/**
 * Service for operations of the dynamic object instance
 * @see DefaultMultiplyDependencyRelationService
 *
 * @author David Suárez
 */
public class DynamicObjectInstanceService<TRelation extends ManyRelation>
        extends DefaultMultiplyDependencyRelationService<DynamicObjectInstance, TRelation>  {

    public DynamicObjectInstanceService(
            @NonNull DynamicObjectInstanceRepository dynamicObjectInstanceRepository,
            @NonNull MultiplyDependencyRelation<? extends Entity, DynamicObjectInstance, TRelation> relation) {

        super(dynamicObjectInstanceRepository, relation);
    }

    /**
     * @see DefaultMultiplyDependencyRelationService#updateEntity(Relation, Relation)
     */
    @Override
    protected void updateEntity(DynamicObjectInstance entityForUpdating, DynamicObjectInstance entity) {
        entityForUpdating.setName(entity.getName());
        entityForUpdating.setDescription(entity.getDescription());
    }
}
