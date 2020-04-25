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
import org.elipcero.carisa.administration.domain.DynamicObjectPrototypeProperty;
import org.elipcero.carisa.core.data.EntityDataState;
import org.elipcero.carisa.core.reactive.data.EmbeddedDependencyRelation;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @see DynamicObjectPrototypePropertyService
 *
 * @author David Suárez
 */
public class DefaultDynamicObjectPrototypePropertyService implements DynamicObjectPrototypePropertyService {

    private final EmbeddedDependencyRelation<DynamicObjectPrototypeProperty> prototypePropertyRelation;

    public DefaultDynamicObjectPrototypePropertyService(
           @NonNull EmbeddedDependencyRelation<DynamicObjectPrototypeProperty> prototypePropertyRelation) {

        this.prototypePropertyRelation = prototypePropertyRelation;
    }

    /**
     * @see DynamicObjectPrototypePropertyService
     */
    @Override
    public Mono<DynamicObjectPrototypeProperty> getById(final Map<String, Object> id) {
        return this.prototypePropertyRelation.getById(id);
    }

    /**
     * @see DynamicObjectPrototypePropertyService
     */
    @Override
    public Mono<DynamicObjectPrototypeProperty> create(final DynamicObjectPrototypeProperty prototypeProperty) {
        return this.prototypePropertyRelation.create(prototypeProperty);
    }

    /**
     * @see DynamicObjectPrototypePropertyService
     */
    @Override
    public Mono<EntityDataState<DynamicObjectPrototypeProperty>> updateOrCreate(
            final DynamicObjectPrototypeProperty prototypeProperty) {

        return this.prototypePropertyRelation
                .updateOrCreate(prototypeProperty,
                        prototypePropertyForUpdating -> {
                            prototypePropertyForUpdating.setName(prototypeProperty.getName());
                            prototypePropertyForUpdating.setDescription(prototypeProperty.getDescription());
                        },
                        () -> this.create(prototypeProperty));
    }
}
