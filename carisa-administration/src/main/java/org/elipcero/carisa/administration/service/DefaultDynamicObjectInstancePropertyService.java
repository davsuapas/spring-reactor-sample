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
import org.elipcero.carisa.administration.domain.DynamicObjectInstanceProperty;
import org.elipcero.carisa.core.data.EntityDataState;
import org.elipcero.carisa.core.reactive.data.EmbeddedDependencyRelation;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @see DynamicObjectInstancePropertyService
 *
 * @author David Suárez
 */
public class DefaultDynamicObjectInstancePropertyService implements DynamicObjectInstancePropertyService {

    private final EmbeddedDependencyRelation<DynamicObjectInstanceProperty<?>> instancePropertyRelation;

    public DefaultDynamicObjectInstancePropertyService(
           @NonNull EmbeddedDependencyRelation<DynamicObjectInstanceProperty<?>> instancePropertyRelation) {

        this.instancePropertyRelation = instancePropertyRelation;
    }

    /**
     * @see DynamicObjectInstancePropertyService
     */
    @Override
    public Mono<DynamicObjectInstanceProperty<?>> getById(final Map<String, Object> id) {
        return this.instancePropertyRelation.getById(id);
    }

    /**
     * @see DynamicObjectInstancePropertyService
     */
    @Override
    public Mono<DynamicObjectInstanceProperty<?>> create(final DynamicObjectInstanceProperty<?> property) {
        return this.instancePropertyRelation.create(property);
    }

    /**
     * @see DynamicObjectInstancePropertyService
     */
    @Override
    public Mono<EntityDataState<DynamicObjectInstanceProperty<?>>> updateOrCreate(
            final DynamicObjectInstanceProperty<?> property) {

        return this.instancePropertyRelation
                .updateOrCreate(property,
                        instancePropertyForUpdating -> {
                        },
                        () -> this.create(property));
    }
}
