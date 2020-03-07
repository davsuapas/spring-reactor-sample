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

package org.elipcero.carisa.administration.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.elipcero.carisa.administration.domain.EnteProperty;
import org.elipcero.carisa.core.data.EntityDataState;
import org.elipcero.carisa.core.reactive.data.EmbeddedDependencyRelation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

/**
 * @see EntePropertyService
 *
 * @author David Suárez
 */
@RequiredArgsConstructor
public class DefaultEntePropertyService implements EntePropertyService {

    @NonNull
    private final EmbeddedDependencyRelation<EnteProperty> entePropertyRelation;

    /**
     * @see EntePropertyService
     */
    @Override
    public Mono<EnteProperty> getById(final Map<String, Object> id) {
        return this.entePropertyRelation.getById(id);
    }

    /**
     * @see EntePropertyService
     */
    @Override
    public Mono<EnteProperty> create(final EnteProperty enteProperty) {
        return this.entePropertyRelation.create(enteProperty);
    }

    /**
     * @see EntePropertyService
     */
    @Override
    public Mono<EntityDataState<EnteProperty>> updateOrCreate(final EnteProperty enteProperty) {
        return this.entePropertyRelation
                .updateOrCreate(enteProperty,
                        entePropertyForUpdating -> {
                            entePropertyForUpdating.setName(enteProperty.getName());
                            entePropertyForUpdating.setType(enteProperty.getType());
                        },
                        this.create(enteProperty));
    }

    /**
     * @see EntePropertyService
     */
    @Override
    public Flux<EnteProperty> getEntePropertiesByEnteId(UUID enteId) {
        return this.entePropertyRelation.getRelationsByParent(enteId);
    }
}
